package org.atonic.cryptexsimple.mapper;

import org.atonic.cryptexsimple.model.dto.CryptoWalletBalanceDTO;
import org.atonic.cryptexsimple.model.entity.CryptoWalletBalance;
import org.atonic.cryptexsimple.model.entity.Cryptocurrency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class CryptoWalletBalanceMapper {

    @Mapping(target = "cryptocurrencyName", source = "cryptocurrency", qualifiedByName = "mapCryptocurrencyToName")
    @Mapping(target = "cryptocurrencySymbol", source = "cryptocurrency", qualifiedByName = "mapCryptocurrencyToSymbol")
    public abstract CryptoWalletBalanceDTO toCryptoWalletBalanceDTO(CryptoWalletBalance cryptoWalletBalance);

    @Named("mapCryptocurrencyToName")
    String mapCryptocurrencyToName(Cryptocurrency source) {
        return source.getSymbol().value;
    }

    @Named("mapCryptocurrencyToSymbol")
    String mapCryptocurrencyToSymbol(Cryptocurrency source) {
        return source.getSymbol().name();
    }
}
