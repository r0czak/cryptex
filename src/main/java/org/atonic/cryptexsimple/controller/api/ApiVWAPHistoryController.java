package org.atonic.cryptexsimple.controller.api;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.response.VWAP.VWAPHistoryResponse;
import org.atonic.cryptexsimple.model.dto.VWAPHistoryDTO;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;
import org.atonic.cryptexsimple.service.ApiKeyService;
import org.atonic.cryptexsimple.service.VWAPService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.atonic.cryptexsimple.controller.api.utils.ApiControllerUtils.API_KEY_HEADER;

@RestController
@RequestMapping("api/v2/vwap")
@AllArgsConstructor
public class ApiVWAPHistoryController {
    private final VWAPService vwapService;
    private final ApiKeyService apiKeyService;

    @PreAuthorize("hasAuthority('API_ACCESS')")
    @GetMapping("/current")
    public ResponseEntity<VWAPHistoryDTO> getCurrentVWAP(
        @RequestHeader(API_KEY_HEADER) String apiKey,
        @RequestParam CryptoSymbol cryptoSymbol,
        @RequestParam FIATSymbol fiatSymbol,
        @RequestParam TimeInterval interval) {
        Optional<User> user = apiKeyService.getUserFromApiKey(UUID.fromString(apiKey));
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<VWAPHistoryDTO> vwap = vwapService.getIntervalVWAP(cryptoSymbol, fiatSymbol, interval);

        return vwap.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('API_ACCESS')")
    @GetMapping("/history")
    public ResponseEntity<VWAPHistoryResponse> getVWAPHistory(
        @RequestHeader(API_KEY_HEADER) String apiKey,
        @RequestParam CryptoSymbol cryptoSymbol,
        @RequestParam FIATSymbol fiatSymbol,
        @RequestParam LocalDateTime startDate,
        @RequestParam LocalDateTime endDate,
        @RequestParam TimeInterval interval,
        @RequestParam int page,
        @RequestParam int size) {
        Optional<User> user = apiKeyService.getUserFromApiKey(UUID.fromString(apiKey));
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size);

        VWAPHistoryResponse response = VWAPHistoryResponse.builder()
            .vwapHistory(vwapService.getHistoricalVWAP(cryptoSymbol, fiatSymbol, startDate, endDate, interval, pageable))
            .build();

        return ResponseEntity.ok(response);
    }
}
