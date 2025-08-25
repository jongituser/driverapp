package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.customer.request.CreateCustomerRequestDTO;
import org.driver.driverapp.dto.customer.request.UpdateCustomerRequestDTO;
import org.driver.driverapp.dto.customer.response.CustomerResponseDTO;
import org.driver.driverapp.model.Customer;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deliveries", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "verified", constant = "false")
    Customer toEntity(CreateCustomerRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deliveries", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "verified", ignore = true)
    void updateEntityFromDto(UpdateCustomerRequestDTO dto, @MappingTarget Customer customer);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "defaultAddressId", ignore = true)
    CustomerResponseDTO toResponseDto(Customer customer);

    List<CustomerResponseDTO> toResponseDtoList(List<Customer> customers);
}
