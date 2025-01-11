package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.request.FIATWalletDepositRequest;
import org.atonic.cryptexsimple.controller.payload.response.MessageResponse;
import org.atonic.cryptexsimple.controller.payload.response.fiat.wallet.UserFIATWalletBalanceResponse;
import org.atonic.cryptexsimple.model.dto.FIATWalletDTO;
import org.atonic.cryptexsimple.model.entity.jpa.FIATWallet;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.service.FIATWalletService;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/fiat-wallet")
@AllArgsConstructor
public class FIATWalletController {
    private final FIATWalletService fiatWalletService;
    private final UserService userService;

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<MessageResponse> addBalanceToFIATWallet(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody FIATWalletDepositRequest request) {
        userService.findUserByAuth0UserId(jwt.getSubject())
            .ifPresent(foundUser -> fiatWalletService.updateBalance(request.getFiatWalletId(), request.getSymbol(), request.getAmount()));

        return ResponseEntity.ok(new MessageResponse(MessageFormat.format("FIAT wallet {0} balance updated for user: {1}",
            request.getSymbol().value,
            jwt.getClaimAsString("email")
        )));
    }

    @PostMapping("/balances")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<UserFIATWalletBalanceResponse> getFIATWalletInfo(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody List<Long> fiatWalletIds) {
        Optional<User> userOptional = userService.getUser(jwt);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<FIATWalletDTO> fiatWallet = fiatWalletService.getFIATWalletDTO(fiatWalletIds.getFirst());

        return fiatWallet.map(fiatWalletDTO -> ResponseEntity.ok(UserFIATWalletBalanceResponse.builder()
            .auth0UserId(jwt.getSubject())
            .wallet(fiatWalletDTO)
            .build()
        )).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/ids")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<Long>> getAllFiatWallets(@AuthenticationPrincipal Jwt jwt) {
        Optional<User> userOptional = userService.getUser(jwt);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<FIATWallet> fiatWallets = fiatWalletService.getUserFIATWallets(userOptional.get());
        if (fiatWallets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(fiatWallets.stream()
            .map(FIATWallet::getId)
            .toList()
        );
    }

}
