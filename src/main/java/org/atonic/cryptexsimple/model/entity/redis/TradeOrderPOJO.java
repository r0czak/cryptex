package org.atonic.cryptexsimple.model.entity.redis;

import lombok.Builder;
import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@RedisHash("trade_order")
public class TradeOrderPOJO {
    @Id
    private String id;

    @Indexed
    private OrderType type;

    private String amount;
    @Indexed
    private String price;

    private String timestamp;

    @Indexed
    private String userId;

    private String cryptoWalletId;
    @Indexed
    private CryptoSymbol cryptoSymbol;

    private String fiatWalletId;
    private FIATSymbol fiatSymbol;
}
