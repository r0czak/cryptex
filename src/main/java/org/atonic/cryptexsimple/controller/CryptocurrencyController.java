package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.mapper.CryptocurrencyMapper;
import org.atonic.cryptexsimple.model.dto.CryptocurrencyDTO;
import org.atonic.cryptexsimple.model.repository.jpa.CryptocurrencyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/cryptocurrency")
@AllArgsConstructor
public class CryptocurrencyController {
    private final CryptocurrencyRepository cryptocurrencyRepository;

    private final CryptocurrencyMapper cryptocurrencyMapper;

    @GetMapping
    public ResponseEntity<List<CryptocurrencyDTO>> getCryptocurrencies() {
        return ResponseEntity.ok(cryptocurrencyRepository.findAll()
            .stream()
            .map(cryptocurrencyMapper::toCryptocurrencyDTO)
            .toList());
    }
}
