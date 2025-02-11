package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.request.orderbook.PlaceOrderRequest;
import org.atonic.cryptexsimple.controller.payload.response.MessageResponse;
import org.atonic.cryptexsimple.controller.utils.OrderbookControllerUtils;
import org.atonic.cryptexsimple.model.entity.jpa.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.jpa.CryptocurrencyRepository;
import org.atonic.cryptexsimple.service.OrderbookService;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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
            TradeOrderPOJO order = OrderbookControllerUtils.prepareTradeOrderPOJO(request, user.get().getId());

            return ResponseEntity.ok(orderbookService.placeOrder(order));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Bad request"));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/buys")
    public ResponseEntity<Page<TradeOrderPOJO>> getBuyOrderBook(CryptoSymbol cryptoSymbol, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderbookService.getBuyTradeOrders(cryptoSymbol, pageable));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/sells")
    public ResponseEntity<Page<TradeOrderPOJO>> getSellOrderBook(CryptoSymbol cryptoSymbol, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderbookService.getSellTradeOrders(cryptoSymbol, pageable));
    }
}
