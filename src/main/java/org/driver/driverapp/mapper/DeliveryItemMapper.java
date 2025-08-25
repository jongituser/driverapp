package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.delivery.request.CreateDeliveryItemRequestDTO;
import org.driver.driverapp.dto.delivery.request.UpdateDeliveryItemRequestDTO;
import org.driver.driverapp.dto.delivery.response.DeliveryItemResponseDTO;
import org.driver.driverapp.model.DeliveryItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "delivery", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DeliveryItem toEntity(CreateDeliveryItemRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "delivery", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateDeliveryItemRequestDTO dto, @MappingTarget DeliveryItem deliveryItem);

    @Mapping(target = "deliveryId", source = "delivery.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productSku", source = "product.sku")
    @Mapping(target = "productCategory", source = "product.category")
    @Mapping(target = "productDescription", source = "product.description")
    @Mapping(target = "productUnit", source = "product.unit")
    @Mapping(target = "hasProduct", expression = "java(deliveryItem.getProduct() != null)")
    DeliveryItemResponseDTO toResponseDTO(DeliveryItem deliveryItem);

    List<DeliveryItemResponseDTO> toResponseDTOList(List<DeliveryItem> deliveryItems);
}
