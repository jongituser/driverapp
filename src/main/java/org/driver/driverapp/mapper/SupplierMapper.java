package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.inventory.request.CreateSupplierRequestDTO;
import org.driver.driverapp.dto.inventory.response.SupplierResponseDTO;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.model.Supplier;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "verified", constant = "false")
    @Mapping(target = "inventoryItems", ignore = true)
    @Mapping(target = "partner", source = "partnerId", qualifiedByName = "partnerFromId")
    Supplier toEntity(CreateSupplierRequestDTO createRequest);

    @Mapping(target = "partnerId", source = "partner.id")
    @Mapping(target = "partnerName", source = "partner.name")
    SupplierResponseDTO toResponseDto(Supplier supplier);

    @Named("partnerFromId")
    default Partner partnerFromId(Long partnerId) {
        if (partnerId == null) return null;
        Partner partner = new Partner();
        partner.setId(partnerId);
        return partner;
    }
}
