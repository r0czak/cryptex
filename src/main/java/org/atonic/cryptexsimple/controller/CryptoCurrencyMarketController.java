package org.atonic.cryptexsimple.controller;


import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.model.dto.CryptocurrencyPriceDTO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.service.PriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/crypto-market")
@AllArgsConstructor
public class CryptoCurrencyMarketController {
    private final PriceService priceService;

    @GetMapping("/price")
    public ResponseEntity<CryptocurrencyPriceDTO> getPrice(@RequestParam String symbol) {
        return ResponseEntity.ok(priceService.getCurrentPrices(CryptoSymbol.valueOf(symbol), LocalDateTime.now().minusMinutes(1), LocalDateTime.now()));
    }
}
