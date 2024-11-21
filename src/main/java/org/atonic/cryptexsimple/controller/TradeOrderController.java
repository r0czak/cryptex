package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.request.PlaceOrderRequest;
import org.atonic.cryptexsimple.controller.payload.response.MessageResponse;
import org.atonic.cryptexsimple.model.entity.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.TradeOrder;
import org.atonic.cryptexsimple.model.entity.User;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.CryptocurrencyRepository;
import org.atonic.cryptexsimple.service.TradeOrderService;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/trade-order")
@AllArgsConstructor
public class TradeOrderController {
    private final TradeOrderService tradeOrderService;
    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final UserService userService;

    @PostMapping("/place")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> placeTradeOrder(@RequestBody PlaceOrderRequest request, @AuthenticationPrincipal Jwt jwt) {
        Optional<Cryptocurrency> crypto = cryptocurrencyRepository.findBySymbol(request.getSymbol());
        Optional<User> user = userService.findUserByAuth0UserId(jwt.getSubject());
        if (crypto.isPresent() && user.isPresent()) {
            TradeOrder order = TradeOrder.builder()
                .type(request.getType())
                .amount(request.getAmount())
                .price(request.getPrice())
                .user(user.get())
                .cryptocurrency(crypto.get())
                .build();

            return ResponseEntity.ok(tradeOrderService.placeOrder(order));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Bad request"));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/orderbook")
    public ResponseEntity<List<TradeOrder>> getOrderBook(CryptoSymbol symbol) {
        return ResponseEntity.ok(tradeOrderService.getOpenTradeOrders(symbol));
    }
}
