package org.atonic.cryptexsimple.controller.api;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.mapper.TradeMapper;
import org.atonic.cryptexsimple.model.dto.TradeDTO;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.atonic.cryptexsimple.service.ApiKeyService;
import org.atonic.cryptexsimple.service.TradeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.atonic.cryptexsimple.controller.api.utils.ApiControllerUtils.API_KEY_HEADER;

@RestController
@RequestMapping("api/v2/trade")
@AllArgsConstructor
public class ApiTradeController {
    private final TradeService tradeService;
    private final ApiKeyService apiKeyService;

    private final TradeMapper tradeMapper;

    @GetMapping("/all")
    public ResponseEntity<Page<TradeDTO>> getAllTrades(@RequestHeader(API_KEY_HEADER) String apiKey,
                                                       @RequestParam(required = false) CryptoSymbol symbol,
                                                       @RequestParam(required = false) FIATSymbol fiatSymbol,
                                                       @RequestParam(required = false) OrderType orderType,
                                                       @RequestParam LocalDateTime from,
                                                       @RequestParam LocalDateTime to,
                                                       @RequestParam int page,
                                                       @RequestParam int size) {
        Optional<User> user = apiKeyService.getUserFromApiKey(UUID.fromString(apiKey));
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        return user.map(value -> ResponseEntity.ok(tradeService.getUserTrades(value, symbol, fiatSymbol, orderType, from, to, pageable)
            .map(tradeMapper::toTradeDTO))).orElseGet(() -> ResponseEntity.badRequest().build());

    }
}
