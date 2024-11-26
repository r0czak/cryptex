package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.request.FIATWalletBalanceRequest;
import org.atonic.cryptexsimple.controller.payload.response.MessageResponse;
import org.atonic.cryptexsimple.controller.payload.response.fiat.wallet.UserFIATWalletBalanceResponse;
import org.atonic.cryptexsimple.model.entity.FIATWallet;
import org.atonic.cryptexsimple.service.FIATWalletService;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
@RequestMapping("api/v1/fiat-wallet")
@AllArgsConstructor
public class FiatWalletController {
    private final FIATWalletService fiatWalletService;
    private final UserService userService;

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<MessageResponse> addBalanceToFIATWallet(@AuthenticationPrincipal Jwt jwt, @RequestBody FIATWalletBalanceRequest request) {
        userService.findUserByAuth0UserId(jwt.getSubject())
            .ifPresent(foundUser -> fiatWalletService.updateBalance(foundUser, request.getAmount()));

        return ResponseEntity.ok(new MessageResponse(MessageFormat.format("FIAT wallet balance updated for user: {0}", jwt.getClaim("email"))));
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<UserFIATWalletBalanceResponse> getFIATWalletBalance(@AuthenticationPrincipal Jwt jwt) {
        FIATWallet fiatWallet = fiatWalletService.getFIATWallet(userService.findUserByAuth0UserId(jwt.getSubject()).get());

        return ResponseEntity.ok(UserFIATWalletBalanceResponse.builder()
            .auth0UserId(fiatWallet.getUser().getAuth0UserId())
            .FIATWalletId(fiatWallet.getId())
            .amount(fiatWallet.getBalance())
            .build()
        );
    }
}
