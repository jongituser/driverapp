package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.invoice.response.InvoiceResponseDTO;
import org.driver.driverapp.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceMapper {

    @Mapping(target = "partnerId", source = "partner.id")
    @Mapping(target = "partnerName", source = "partner.name")
    @Mapping(target = "remainingAmount", expression = "java(invoice.getRemainingAmount())")
    InvoiceResponseDTO toResponseDTO(Invoice invoice);

    List<InvoiceResponseDTO> toResponseDTOList(List<Invoice> invoices);
}
