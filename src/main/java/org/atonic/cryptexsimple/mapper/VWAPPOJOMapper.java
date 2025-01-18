package org.atonic.cryptexsimple.mapper;

import lombok.Generated;
import org.atonic.cryptexsimple.model.dto.VWAPHistoryDTO;
import org.atonic.cryptexsimple.model.entity.redis.VWAPPOJO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Generated()
@Mapper(componentModel = "spring")
public interface VWAPPOJOMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "cryptoSymbol", source = "cryptoSymbol")
    @Mapping(target = "fiatSymbol", source = "fiatSymbol")
    @Mapping(target = "timestamp", source = "lastUpdated")
    @Mapping(target = "tradingDate", source = "tradingDate")
    @Mapping(target = "timeInterval", source = "timeInterval")
    @Mapping(target = "vwap", source = "currentVWAP")
    @Mapping(target = "totalVolume", source = "totalVolume")
    @Mapping(target = "sumPriceVolume", source = "sumPriceVolume")
    @Mapping(target = "openPrice", source = "openPrice")
    @Mapping(target = "closePrice", source = "closePrice")
    @Mapping(target = "highPrice", source = "highPrice")
    @Mapping(target = "lowPrice", source = "lowPrice")
    VWAPHistoryDTO toVWAPHistoryDTO(VWAPPOJO vwappojo);

    @InheritInverseConfiguration
    VWAPPOJO toVWAPPOJO(VWAPHistoryDTO vwapHistoryDTO);
}
