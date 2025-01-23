package org.atonic.cryptexsimple.mapper;

import lombok.Generated;
import org.atonic.cryptexsimple.model.dto.VWAPHistoryDTO;
import org.atonic.cryptexsimple.model.entity.jpa.VWAPHistory;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Generated()
@Mapper(componentModel = "spring")
public interface VWAPHistoryMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "cryptoSymbol", source = "cryptoSymbol")
    @Mapping(target = "fiatSymbol", source = "fiatSymbol")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "tradingDate", source = "tradingDate")
    @Mapping(target = "timeInterval", source = "timeInterval")
    @Mapping(target = "vwap", source = "vwap")
    @Mapping(target = "totalVolume", source = "totalVolume")
    @Mapping(target = "sumPriceVolume", source = "sumPriceVolume")
    @Mapping(target = "openPrice", source = "openPrice")
    @Mapping(target = "closePrice", source = "closePrice")
    @Mapping(target = "highPrice", source = "highPrice")
    @Mapping(target = "lowPrice", source = "lowPrice")
    VWAPHistoryDTO toVWAPHistoryDTO(VWAPHistory vwapHistory);

    @InheritInverseConfiguration
    VWAPHistory toVWAPHistory(VWAPHistoryDTO vwapHistoryDTO);
}

