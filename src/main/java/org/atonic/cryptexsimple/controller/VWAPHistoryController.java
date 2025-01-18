package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.response.VWAP.VWAPHistoryResponse;
import org.atonic.cryptexsimple.model.dto.VWAPHistoryDTO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;
import org.atonic.cryptexsimple.service.UserService;
import org.atonic.cryptexsimple.service.VWAPService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/vwap")
@AllArgsConstructor
public class VWAPHistoryController {
    private final VWAPService vwapService;
    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/current")
    public ResponseEntity<VWAPHistoryDTO> getCurrentVWAP(
        @RequestParam CryptoSymbol cryptoSymbol,
        @RequestParam FIATSymbol fiatSymbol,
        @RequestParam TimeInterval interval) {
        Optional<VWAPHistoryDTO> vwap = vwapService.getIntervalVWAP(cryptoSymbol, fiatSymbol, interval);

        return vwap.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/history")
    public ResponseEntity<VWAPHistoryResponse> getVWAPHistory(
        @RequestParam CryptoSymbol cryptoSymbol,
        @RequestParam FIATSymbol fiatSymbol,
        @RequestParam LocalDateTime startDate,
        @RequestParam LocalDateTime endDate,
        @RequestParam TimeInterval interval,
        @RequestParam int page,
        @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);

        VWAPHistoryResponse response = VWAPHistoryResponse.builder()
            .vwapHistory(vwapService.getHistoricalVWAP(cryptoSymbol, fiatSymbol, startDate, endDate, interval, pageable))
            .build();

        return ResponseEntity.ok(response);
    }
}
