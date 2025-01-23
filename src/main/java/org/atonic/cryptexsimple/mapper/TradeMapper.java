package org.atonic.cryptexsimple.mapper;

import lombok.Generated;
import org.atonic.cryptexsimple.model.dto.TradeDTO;
import org.atonic.cryptexsimple.model.entity.jpa.Trade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Generated()
@Mapper(componentModel = "spring")
public interface TradeMapper {

    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "sellerFIATWalletId", source = "sellerFIATWallet.id")
    @Mapping(target = "sellerCryptoWalletId", source = "sellerCryptoWallet.id")
    @Mapping(target = "buyerFIATWalletId", source = "buyerFIATWallet.id")
    @Mapping(target = "buyerCryptoWalletId", source = "buyerCryptoWallet.id")
    @Mapping(target = "fiatSymbol", source = "fiatCurrency.symbol")
    @Mapping(target = "cryptoSymbol", source = "cryptocurrency.symbol")
    TradeDTO toTradeDTO(Trade trade);
}
