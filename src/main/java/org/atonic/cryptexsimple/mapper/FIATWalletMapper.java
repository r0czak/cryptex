package org.atonic.cryptexsimple.mapper;

import lombok.Generated;
import org.atonic.cryptexsimple.model.dto.FIATWalletBalanceDTO;
import org.atonic.cryptexsimple.model.dto.FIATWalletDTO;
import org.atonic.cryptexsimple.model.entity.jpa.FIATWallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Generated()
@Mapper(componentModel = "spring")
public interface FIATWalletMapper {

    @Mapping(target = "fiatWalletId", source = "fiatWallet.id")
    @Mapping(target = "balances", source = "balances")
    FIATWalletDTO toFIATWalletDTO(FIATWallet fiatWallet, List<FIATWalletBalanceDTO> balances);
}
