package org.atonic.cryptexsimple.mapper;

import lombok.Generated;
import org.atonic.cryptexsimple.model.dto.CryptocurrencyDTO;
import org.atonic.cryptexsimple.model.entity.jpa.Cryptocurrency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Generated()
@Mapper(componentModel = "spring")
public interface CryptocurrencyMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "cryptoSymbol", source = "symbol")
    @Mapping(target = "cryptoName", source = "symbol.value")
    CryptocurrencyDTO toCryptocurrencyDTO(Cryptocurrency cryptocurrency);
}
