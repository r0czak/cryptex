package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.request.CryptoWalletBalanceRequest;
import org.atonic.cryptexsimple.controller.payload.request.FIATWalletBalanceRequest;
import org.atonic.cryptexsimple.controller.payload.response.MessageResponse;
import org.atonic.cryptexsimple.controller.payload.response.UserCryptoWalletBalanceResponse;
import org.atonic.cryptexsimple.controller.payload.response.UserFIATWalletBalanceResponse;
import org.atonic.cryptexsimple.model.dto.CryptoWalletBalanceDTO;
import org.atonic.cryptexsimple.model.entity.CryptoWallet;
import org.atonic.cryptexsimple.model.entity.CryptoWalletBalance;
import org.atonic.cryptexsimple.model.entity.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.FIATWallet;
import org.atonic.cryptexsimple.model.repository.CryptocurrencyRepository;
import org.atonic.cryptexsimple.service.CryptoWalletService;
import org.atonic.cryptexsimple.service.FIATWalletService;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/crypto-wallet")
@AllArgsConstructor
public class CryptoWalletController {
    private final CryptoWalletService cryptoWalletService;
    private final UserService userService;

    private final CryptocurrencyRepository cryptocurrencyRepository;

    @PostMapping("/balance")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> addBalanceToFIATWallet(Principal principal, @RequestBody CryptoWalletBalanceRequest request) {
        userService.findByUserName(principal.getName())
            .ifPresent(foundUser -> cryptoWalletService.updateBalance(foundUser, request.getSymbol(), request.getAmount()));

        return ResponseEntity.ok(new MessageResponse(
            MessageFormat.format("{0} crypto wallet balance updated for user: {1}", request.getSymbol().value, principal.getName())
            )
        );
    }

    @GetMapping("/balance")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserCryptoWalletBalanceResponse> getCryptoWalletBalances(Principal principal) {
        CryptoWallet cryptoWallet = cryptoWalletService.getCryptoWallet(userService.findByUserName(principal.getName()).get());

        List<CryptoWalletBalanceDTO> balances = getCryptoBalancesFromCryptoWallet(cryptoWallet);

        return ResponseEntity.ok(UserCryptoWalletBalanceResponse.builder()
            .userName(cryptoWallet.getUser().getUsername())
            .cryptoWalletId(cryptoWallet.getId())
            .balances(balances)
            .build()
        );
    }

    private List<CryptoWalletBalanceDTO> getCryptoBalancesFromCryptoWallet(CryptoWallet cryptoWallet) {
        List<CryptoWalletBalanceDTO> balances = new ArrayList<>();
        List<Cryptocurrency> cryptos = cryptocurrencyRepository.findAll();
        for (Cryptocurrency crypto : cryptos) {
            balances.add(
                CryptoWalletBalanceDTO.builder()
                    .cryptocurrency(crypto.getSymbol().value)
                    .balance(cryptoWalletService.getBalance(cryptoWallet.getUser(), crypto.getSymbol()).getBalance())
                    .build()
            );
        }

        return balances;
    }
}
