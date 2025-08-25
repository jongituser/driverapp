package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.wallet.response.WalletResponseDTO;
import org.driver.driverapp.model.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WalletMapper {

    WalletResponseDTO toResponseDTO(Wallet wallet);

    List<WalletResponseDTO> toResponseDTOList(List<Wallet> wallets);
}
