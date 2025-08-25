package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.wallet.response.WalletTransactionResponseDTO;
import org.driver.driverapp.model.WalletTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WalletTransactionMapper {

    WalletTransactionResponseDTO toResponseDTO(WalletTransaction walletTransaction);

    List<WalletTransactionResponseDTO> toResponseDTOList(List<WalletTransaction> walletTransactions);
}
