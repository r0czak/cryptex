package org.atonic.cryptexsimple.mapper;

import org.atonic.cryptexsimple.model.dto.CryptoWalletBalanceDTO;
import org.atonic.cryptexsimple.model.entity.CryptoWalletBalance;
import org.atonic.cryptexsimple.model.entity.Cryptocurrency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class CryptoWalletBalanceMapper {

    @Mapping(target = "cryptocurrency", source = "cryptocurrency", qualifiedByName = "mapCryptocurrency")
    public abstract CryptoWalletBalanceDTO toCryptoWalletBalanceDTO(CryptoWalletBalance cryptoWalletBalance);

    @Named("mapCryptocurrency")
    String mapCryptocurrency(Cryptocurrency source) {
        return source.getSymbol().value;
    }
}
