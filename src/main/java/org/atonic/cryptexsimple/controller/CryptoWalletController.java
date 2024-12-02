package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.request.CryptoWalletBalanceRequest;
import org.atonic.cryptexsimple.controller.payload.response.MessageResponse;
import org.atonic.cryptexsimple.controller.payload.response.crypto.wallet.UserCryptoWalletBalanceResponse;
import org.atonic.cryptexsimple.model.dto.CryptoWalletDTO;
import org.atonic.cryptexsimple.model.entity.CryptoWallet;
import org.atonic.cryptexsimple.model.entity.User;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.CryptocurrencyRepository;
import org.atonic.cryptexsimple.service.CryptoWalletService;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/crypto-wallet")
@AllArgsConstructor
public class CryptoWalletController {
    private final CryptoWalletService cryptoWalletService;
    private final UserService userService;

    private final CryptocurrencyRepository cryptocurrencyRepository;

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<MessageResponse> createCryptoWallet(@AuthenticationPrincipal Jwt jwt, @RequestBody Map<String, String> data) {
        Optional<User> optionalUser = userService.getUser(jwt);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<CryptoWallet> cryptoWalletOptional = cryptoWalletService.createNewWallet(optionalUser.get(), data.get("walletName"));
        if (cryptoWalletOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse(
                MessageFormat.format("Crypto wallet with name: {0} couldn''t be created", data.get("walletName")))
            );
        }
        return ResponseEntity.ok(new MessageResponse(
            MessageFormat.format("Crypto wallet with name: {0} was created", data.get("walletName")))
        );
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<MessageResponse> depositCryptoToCryptoWallet(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody CryptoWalletBalanceRequest request) {
        userService.findUserByAuth0UserId(jwt.getSubject())
            .ifPresent(foundUser -> cryptoWalletService.updateBalance(foundUser, request.getCryptoWalletId(), CryptoSymbol.valueOf(request.getSymbol()), request.getAmount()));

        return ResponseEntity.ok(new MessageResponse(
                MessageFormat.format("{0} crypto wallet balance updated for user: {1}", request.getSymbol(), jwt.getClaim("email"))
            )
        );
    }

    @PostMapping("/balances")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<UserCryptoWalletBalanceResponse> getCryptoWalletBalances(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody List<Long> walletIds) {
        Optional<User> userOptional = userService.getUser(jwt);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<CryptoWalletDTO> cryptoWallets = new ArrayList<>();

        walletIds.forEach(id -> cryptoWalletService.getCryptoWallet(userOptional.get(), id).ifPresent(cryptoWallets::add));

        return ResponseEntity.ok(UserCryptoWalletBalanceResponse.builder()
            .auth0UserId(jwt.getSubject())
            .wallets(cryptoWallets)
            .build()
        );
    }

    @GetMapping("/ids")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<Long>> getAllCryptoWalletIds(@AuthenticationPrincipal Jwt jwt) {
        Optional<User> userOptional = userService.getUser(jwt);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<CryptoWallet> cryptoWallets = cryptoWalletService.getCryptoWallets(userOptional.get());
        if (cryptoWallets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(cryptoWallets.stream()
            .map(CryptoWallet::getId)
            .toList()
        );
    }
}
