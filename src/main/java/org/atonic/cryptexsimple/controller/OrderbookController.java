package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.request.PlaceOrderRequest;
import org.atonic.cryptexsimple.controller.payload.response.MessageResponse;
import org.atonic.cryptexsimple.model.entity.jpa.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.jpa.CryptocurrencyRepository;
import org.atonic.cryptexsimple.service.OrderbookService;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/orderbook")
@AllArgsConstructor
public class OrderbookController {
    private final OrderbookService orderbookService;
    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final UserService userService;

    @PostMapping("/place")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> placeTradeOrder(@RequestBody PlaceOrderRequest request, @AuthenticationPrincipal Jwt jwt) {
        Optional<Cryptocurrency> crypto = cryptocurrencyRepository.findBySymbol(request.getCryptoSymbol());
        Optional<User> user = userService.findUserByAuth0UserId(jwt.getSubject());
        if (crypto.isPresent() && user.isPresent()) {
            TradeOrderPOJO order = TradeOrderPOJO.builder()
                .type(request.getType())
                .amount(request.getAmount().toString())
                .price(request.getPrice().toString())
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .userId(user.get().getId().toString())
                .cryptoWalletId(request.getCryptoWalletId().toString())
                .cryptoSymbol(request.getCryptoSymbol())
                .fiatWalletId(request.getFiatWalletId().toString())
                .fiatSymbol(request.getFiatSymbol())
                .build();

            return ResponseEntity.ok(orderbookService.placeOrder(order));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Bad request"));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/buys")
    public ResponseEntity<List<TradeOrderPOJO>> getBuyOrderBook(CryptoSymbol symbol, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderbookService.getBuyTradeOrders(symbol, pageable));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/sells")
    public ResponseEntity<List<TradeOrderPOJO>> getSellOrderBook(CryptoSymbol symbol, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderbookService.getSellTradeOrders(symbol, pageable));
    }
}
