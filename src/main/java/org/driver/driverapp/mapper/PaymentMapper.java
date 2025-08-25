package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.payment.response.PaymentResponseDTO;
import org.driver.driverapp.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.fullName")
    @Mapping(target = "deliveryId", source = "delivery.id")
    @Mapping(target = "deliveryCode", source = "delivery.deliveryCode")
    PaymentResponseDTO toResponseDTO(Payment payment);

    List<PaymentResponseDTO> toResponseDTOList(List<Payment> payments);
}
