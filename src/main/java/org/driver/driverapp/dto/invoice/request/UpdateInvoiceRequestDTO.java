package org.driver.driverapp.dto.invoice.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvoiceRequestDTO {

    @DecimalMin("0.01")
    private BigDecimal totalAmount;

    private LocalDate dueDate;

    private InvoiceStatus status;

    @Size(max = 1000)
    private String description;
}
