package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.request.PlaceOrderRequest;
import org.atonic.cryptexsimple.controller.payload.response.MessageResponse;
import org.atonic.cryptexsimple.model.entity.jpa.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.jpa.TradeOrder;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.jpa.CryptocurrencyRepository;
import org.atonic.cryptexsimple.service.RedisTradeOrderService;
import org.atonic.cryptexsimple.service.TradeOrderService;
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
@RequestMapping("api/v1/trade-order")
@AllArgsConstructor
public class TradeOrderController {
    private final TradeOrderService tradeOrderService;
    private final RedisTradeOrderService redisTradeOrderService;
    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final UserService userService;

    @PostMapping("/place")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> placeTradeOrder(@RequestBody PlaceOrderRequest request, @AuthenticationPrincipal Jwt jwt) {
        Optional<Cryptocurrency> crypto = cryptocurrencyRepository.findBySymbol(request.getCryptoSymbol());
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

    @PostMapping("/place-order")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> placeTradeOrderV2(@RequestBody PlaceOrderRequest request, @AuthenticationPrincipal Jwt jwt) {
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

            return ResponseEntity.ok(redisTradeOrderService.placeOrder(order));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Bad request"));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/orderbook/buy")
    public ResponseEntity<List<TradeOrderPOJO>> getBuyOrderBook(CryptoSymbol symbol, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(redisTradeOrderService.getBuyTradeOrders(symbol, pageable));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/orderbook/sell")
    public ResponseEntity<List<TradeOrderPOJO>> getSellOrderBook(CryptoSymbol symbol, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(redisTradeOrderService.getSellTradeOrders(symbol, pageable));
    }
}
