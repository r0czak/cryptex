package org.atonic.cryptexsimple.controller.api;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.request.orderbook.PlaceOrderRequest;
import org.atonic.cryptexsimple.controller.payload.response.MessageResponse;
import org.atonic.cryptexsimple.controller.utils.OrderbookControllerUtils;
import org.atonic.cryptexsimple.model.entity.jpa.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.jpa.CryptocurrencyRepository;
import org.atonic.cryptexsimple.service.ApiKeyService;
import org.atonic.cryptexsimple.service.OrderbookService;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.atonic.cryptexsimple.controller.api.utils.ApiControllerUtils.API_KEY_HEADER;

@RestController
@RequestMapping("api/v2/orderbook")
@AllArgsConstructor
public class ApiOrderbookController {
    private final OrderbookService orderbookService;
    private final UserService userService;
    private final ApiKeyService apiKeyService;

    private final CryptocurrencyRepository cryptocurrencyRepository;


    @PostMapping("/place")
    @PreAuthorize("hasAuthority('API_ACCESS')")
    public ResponseEntity<?> placeTradeOrder(@RequestHeader(API_KEY_HEADER) String apiKey,
                                             @RequestBody PlaceOrderRequest request) {
        Optional<User> user = apiKeyService.getUserFromApiKey(UUID.fromString(apiKey));
        Optional<Cryptocurrency> crypto = cryptocurrencyRepository.findBySymbol(request.getCryptoSymbol());

        if (crypto.isPresent() && user.isPresent()) {
            TradeOrderPOJO order = OrderbookControllerUtils.prepareTradeOrderPOJO(request, user.get().getId());

            return ResponseEntity.ok(orderbookService.placeOrder(order));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Bad request"));
    }

    @GetMapping("/buys")
    @PreAuthorize("hasAuthority('API_ACCESS')")
    public ResponseEntity<Page<TradeOrderPOJO>> getBuyOrderBook(@RequestHeader(API_KEY_HEADER) String apiKey,
                                                                CryptoSymbol symbol, int page, int size) {
        Optional<User> user = apiKeyService.getUserFromApiKey(UUID.fromString(apiKey));
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(Page.empty());
        }
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderbookService.getBuyTradeOrders(symbol, pageable));
    }

    @GetMapping("/sells")
    @PreAuthorize("hasAuthority('API_ACCESS')")
    public ResponseEntity<Page<TradeOrderPOJO>> getSellOrderBook(@RequestHeader(API_KEY_HEADER) String apiKey,
                                                                 CryptoSymbol symbol, int page, int size) {
        Optional<User> user = apiKeyService.getUserFromApiKey(UUID.fromString(apiKey));
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(Page.empty());
        }

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderbookService.getSellTradeOrders(symbol, pageable));
    }

}
