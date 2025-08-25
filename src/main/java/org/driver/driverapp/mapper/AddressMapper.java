package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.address.request.CreateAddressRequestDTO;
import org.driver.driverapp.dto.address.request.UpdateAddressRequestDTO;
import org.driver.driverapp.dto.address.response.AddressResponseDTO;
import org.driver.driverapp.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postalCode", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Address toEntity(CreateAddressRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postalCode", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Address updateEntityFromDto(UpdateAddressRequestDTO dto, @MappingTarget Address address);

    @Mapping(target = "gpsCoordinates", source = ".", qualifiedByName = "formatGpsCoordinates")
    @Mapping(target = "formattedAddress", source = ".", qualifiedByName = "formatAddress")
    @Mapping(target = "addressType", source = ".", qualifiedByName = "determineAddressType")
    @Mapping(target = "postalCode", source = "postalCode.code")
    @Mapping(target = "postalCodeId", source = "postalCode.id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "partnerId", source = "partner.id")
    AddressResponseDTO toResponseDTO(Address address);

    List<AddressResponseDTO> toResponseDTOList(List<Address> addresses);

    @Named("formatGpsCoordinates")
    default String formatGpsCoordinates(Address address) {
        return address.getGpsCoordinates();
    }

    @Named("formatAddress")
    default String formatAddress(Address address) {
        return address.getFormattedAddress();
    }

    @Named("determineAddressType")
    default String determineAddressType(Address address) {
        if (address.isGpsOnly()) {
            return "GPS_ONLY";
        } else if (address.isFullEthiopianAddress()) {
            return "FULL_ETHIOPIAN";
        } else if (address.hasGpsCoordinates() && address.hasEthiopianAddress()) {
            return "HYBRID";
        } else {
            return "INVALID";
        }
    }
}
