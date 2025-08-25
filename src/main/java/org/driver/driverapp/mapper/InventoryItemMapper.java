package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.inventory.request.CreateInventoryItemRequestDTO;
import org.driver.driverapp.dto.inventory.request.UpdateInventoryItemRequestDTO;
import org.driver.driverapp.dto.inventory.response.InventoryItemResponseDTO;
import org.driver.driverapp.model.InventoryItem;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.model.Supplier;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "lowStockAlert", expression = "java(createRequest.getQuantity() <= createRequest.getMinimumStockThreshold())")
    @Mapping(target = "expired", expression = "java(createRequest.getExpiryDate() != null && createRequest.getExpiryDate().isBefore(java.time.LocalDate.now()))")
    @Mapping(target = "totalValue", expression = "java(createRequest.getUnitPrice() != null ? createRequest.getUnitPrice().multiply(java.math.BigDecimal.valueOf(createRequest.getQuantity())) : null)")
    @Mapping(target = "partner", source = "partnerId", qualifiedByName = "partnerFromId")
    @Mapping(target = "supplier", source = "supplierId", qualifiedByName = "supplierFromId")
    InventoryItem toEntity(CreateInventoryItemRequestDTO createRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "sku", ignore = true) // SKU should not be updated
    @Mapping(target = "partner", ignore = true) // Partner should not be updated
    @Mapping(target = "lowStockAlert", expression = "java(updateRequest.getQuantity() != null ? updateRequest.getQuantity() <= inventoryItem.getMinimumStockThreshold() : inventoryItem.isLowStock())")
    @Mapping(target = "expired", expression = "java(updateRequest.getExpiryDate() != null ? updateRequest.getExpiryDate().isBefore(java.time.LocalDate.now()) : inventoryItem.isExpired())")
    @Mapping(target = "totalValue", expression = "java(updateRequest.getUnitPrice() != null && updateRequest.getQuantity() != null ? updateRequest.getUnitPrice().multiply(java.math.BigDecimal.valueOf(updateRequest.getQuantity())) : (updateRequest.getUnitPrice() != null ? updateRequest.getUnitPrice().multiply(java.math.BigDecimal.valueOf(inventoryItem.getQuantity())) : (updateRequest.getQuantity() != null && inventoryItem.getUnitPrice() != null ? inventoryItem.getUnitPrice().multiply(java.math.BigDecimal.valueOf(updateRequest.getQuantity())) : inventoryItem.getTotalValue())))")
    @Mapping(target = "supplier", source = "supplierId", qualifiedByName = "supplierFromId")
    void updateEntityFromDto(UpdateInventoryItemRequestDTO updateRequest, @MappingTarget InventoryItem inventoryItem);

    @Mapping(target = "partnerId", source = "partner.id")
    @Mapping(target = "partnerName", source = "partner.name")
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "isLowStock", expression = "java(inventoryItem.isLowStock())")
    @Mapping(target = "isExpiringSoon", expression = "java(inventoryItem.isExpiringSoon(30))")
    @Mapping(target = "daysUntilExpiry", expression = "java(calculateDaysUntilExpiry(inventoryItem.getExpiryDate()))")
    InventoryItemResponseDTO toResponseDto(InventoryItem inventoryItem);

    @Named("partnerFromId")
    default Partner partnerFromId(Long partnerId) {
        if (partnerId == null) return null;
        Partner partner = new Partner();
        partner.setId(partnerId);
        return partner;
    }

    @Named("supplierFromId")
    default Supplier supplierFromId(Long supplierId) {
        if (supplierId == null) return null;
        Supplier supplier = new Supplier();
        supplier.setId(supplierId);
        return supplier;
    }

    default int calculateDaysUntilExpiry(LocalDate expiryDate) {
        if (expiryDate == null) return -1;
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
}
