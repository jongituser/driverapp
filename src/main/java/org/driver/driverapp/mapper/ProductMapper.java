package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.product.request.CreateProductRequestDTO;
import org.driver.driverapp.dto.product.request.UpdateProductRequestDTO;
import org.driver.driverapp.dto.product.response.ProductResponseDTO;
import org.driver.driverapp.model.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "deliveryItems", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(CreateProductRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "deliveryItems", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateProductRequestDTO dto, @MappingTarget Product product);

    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "supplierPhone", source = "supplier.phone")
    @Mapping(target = "supplierEmail", source = "supplier.email")
    @Mapping(target = "hasSupplier", expression = "java(product.hasSupplier())")
    ProductResponseDTO toResponseDTO(Product product);

    List<ProductResponseDTO> toResponseDTOList(List<Product> products);
}
