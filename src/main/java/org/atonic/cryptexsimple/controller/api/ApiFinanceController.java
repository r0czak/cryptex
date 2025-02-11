package org.atonic.cryptexsimple.controller.api;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.response.finance.ApiFinanceInfoResponse;
import org.atonic.cryptexsimple.model.dto.CryptoWalletDTO;
import org.atonic.cryptexsimple.model.dto.FIATWalletDTO;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.service.ApiKeyService;
import org.atonic.cryptexsimple.service.CryptoWalletService;
import org.atonic.cryptexsimple.service.FIATWalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.atonic.cryptexsimple.controller.api.utils.ApiControllerUtils.API_KEY_HEADER;

@RestController
@RequestMapping("api/v2/finance")
@AllArgsConstructor
public class ApiFinanceController {
    private final ApiKeyService apiKeyService;
    private final CryptoWalletService cryptoWalletService;
    private final FIATWalletService fiatWalletService;

    @GetMapping("/info")
    public ResponseEntity<ApiFinanceInfoResponse> getFinanceInfo(@RequestHeader(API_KEY_HEADER) String apiKey) {
        Optional<User> user = apiKeyService.getUserFromApiKey(UUID.fromString(apiKey));
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<CryptoWalletDTO> cryptoWalletDTOS = cryptoWalletService.getUserCryptoWallets(user.get()).stream()
            .map(cw -> cryptoWalletService.getCryptoWalletDTO(cw.getId()).get())
            .toList();

        List<FIATWalletDTO> fiatWalletDTOS = fiatWalletService.getUserFIATWallets(user.get()).stream()
            .map(fw -> fiatWalletService.getFIATWalletDTO(fw.getId()).get())
            .toList();

        return ResponseEntity.ok(ApiFinanceInfoResponse.builder()
            .cryptoWallets(cryptoWalletDTOS)
            .fiatWallets(fiatWalletDTOS)
            .build());

    }
}
