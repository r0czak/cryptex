package org.atonic.cryptexsimple.mapper;

import org.atonic.cryptexsimple.model.dto.FIATWalletBalanceDTO;
import org.atonic.cryptexsimple.model.entity.FIATCurrency;
import org.atonic.cryptexsimple.model.entity.FIATWalletBalance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class FIATWalletBalanceMapper {

    @Mapping(target = "fiatCurrencyName", source = "fiatCurrency", qualifiedByName = "mapFiatCurrencyToName")
    @Mapping(target = "fiatCurrencySymbol", source = "fiatCurrency", qualifiedByName = "mapFiatCurrencyToSymbol")
    public abstract FIATWalletBalanceDTO toFIATWalletBalanceDTO(FIATWalletBalance cryptoWalletBalance);

    @Named("mapFiatCurrencyToName")
    String mapFiatCurrencyToName(FIATCurrency source) {
        return source.getSymbol().value;
    }

    @Named("mapFiatCurrencyToSymbol")
    String mapFiatCurrencyToSymbol(FIATCurrency source) {
        return source.getSymbol().name();
    }
}
