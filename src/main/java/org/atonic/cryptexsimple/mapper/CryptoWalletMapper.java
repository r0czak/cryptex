package org.atonic.cryptexsimple.mapper;

import lombok.Generated;
import org.atonic.cryptexsimple.model.dto.CryptoWalletBalanceDTO;
import org.atonic.cryptexsimple.model.dto.CryptoWalletDTO;
import org.atonic.cryptexsimple.model.entity.jpa.CryptoWallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Generated()
@Mapper(componentModel = "spring")
public interface CryptoWalletMapper {

    @Mapping(target = "cryptoWalletId", source = "cryptoWallet.id")
    @Mapping(target = "balances", source = "balances")
    CryptoWalletDTO toCryptoWalletDTO(CryptoWallet cryptoWallet, List<CryptoWalletBalanceDTO> balances);
}
