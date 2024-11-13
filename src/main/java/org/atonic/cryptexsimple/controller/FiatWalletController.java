package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.request.FIATWalletBalanceRequest;
import org.atonic.cryptexsimple.controller.payload.response.MessageResponse;
import org.atonic.cryptexsimple.controller.payload.response.UserFIATWalletBalanceResponse;
import org.atonic.cryptexsimple.model.entity.FIATWallet;
import org.atonic.cryptexsimple.service.FIATWalletService;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.MessageFormat;

@RestController
@RequestMapping("api/v1/fiat-wallet")
@AllArgsConstructor
public class FiatWalletController {
    private final FIATWalletService fiatWalletService;
    private final UserService userService;

    @PostMapping("/balance")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> addBalanceToFIATWallet(Principal principal, @RequestBody FIATWalletBalanceRequest request) {
        userService.findByUserName(principal.getName())
            .ifPresent(foundUser -> fiatWalletService.updateBalance(foundUser, request.getAmount()));

        return ResponseEntity.ok(new MessageResponse(MessageFormat.format("FIAT wallet balance updated for user: {0}", principal.getName())));
    }

    @GetMapping("/balance")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserFIATWalletBalanceResponse> getFIATWalletBalance(Principal principal) {
        FIATWallet fiatWallet = fiatWalletService.getFIATWallet(userService.findByUserName(principal.getName()).get());

        return ResponseEntity.ok(UserFIATWalletBalanceResponse.builder()
            .userName(fiatWallet.getUser().getUsername())
            .FIATWalletId(fiatWallet.getId())
            .amount(fiatWallet.getBalance())
            .build()
        );
    }
}
