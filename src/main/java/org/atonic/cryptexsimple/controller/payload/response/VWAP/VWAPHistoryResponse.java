package org.atonic.cryptexsimple.controller.payload.response.VWAP;

import lombok.Builder;
import lombok.Data;
import org.atonic.cryptexsimple.model.dto.VWAPHistoryDTO;
import org.springframework.data.domain.Page;

@Data
@Builder
public class VWAPHistoryResponse {
    Page<VWAPHistoryDTO> vwapHistory;
}
