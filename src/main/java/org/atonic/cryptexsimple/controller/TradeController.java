package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.mapper.TradeMapper;
import org.atonic.cryptexsimple.model.dto.TradeDTO;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.atonic.cryptexsimple.service.TradeService;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/trade")
@AllArgsConstructor
public class TradeController {
    private final TradeService tradeService;
    private final UserService userService;

    private final TradeMapper tradeMapper;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Page<TradeDTO>> getAllTrades(@RequestParam(required = false) CryptoSymbol symbol,
                                                       @RequestParam(required = false) FIATSymbol fiatSymbol,
                                                       @RequestParam(required = false) OrderType orderType,
                                                       @RequestParam LocalDateTime from,
                                                       @RequestParam LocalDateTime to,
                                                       @RequestParam int page,
                                                       @RequestParam int size,
                                                       @AuthenticationPrincipal Jwt jwt) {
        Optional<User> user = userService.findUserByAuth0UserId(jwt.getSubject());
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        return user.map(value -> ResponseEntity.ok(tradeService.getUserTrades(value, symbol, fiatSymbol, orderType, from, to, pageable)
            .map(tradeMapper::toTradeDTO))).orElseGet(() -> ResponseEntity.badRequest().build());

    }
}
