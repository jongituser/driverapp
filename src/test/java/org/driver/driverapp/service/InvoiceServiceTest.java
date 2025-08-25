package org.driver.driverapp.service;

import org.driver.driverapp.dto.invoice.request.CreateInvoiceRequestDTO;
import org.driver.driverapp.dto.invoice.request.UpdateInvoiceRequestDTO;
import org.driver.driverapp.dto.invoice.response.InvoiceResponseDTO;
import org.driver.driverapp.enums.InvoiceStatus;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.InvoiceMapper;
import org.driver.driverapp.model.Invoice;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.repository.InvoiceRepository;
import org.driver.driverapp.repository.PartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private InvoiceService invoiceService;

    private Partner testPartner;
    private Invoice testInvoice;
    private InvoiceResponseDTO testInvoiceResponseDTO;

    @BeforeEach
    void setUp() {
        testPartner = Partner.builder()
                .id(1L)
                .name("Test Partner")
                .phone("+251912345678")
                .email("partner@example.com")
                .build();

        testInvoice = Invoice.builder()
                .id(1L)
                .partner(testPartner)
                .invoiceNumber("INV-20240215-0001-001")
                .totalAmount(BigDecimal.valueOf(5000.00))
                .dueDate(LocalDate.of(2024, 2, 15))
                .status(InvoiceStatus.DRAFT)
                .description("Monthly service invoice")
                .paidAmount(BigDecimal.ZERO)
                .active(true)
                .build();

        testInvoiceResponseDTO = InvoiceResponseDTO.builder()
                .id(1L)
                .partnerId(1L)
                .partnerName("Test Partner")
                .invoiceNumber("INV-20240215-0001-001")
                .totalAmount(BigDecimal.valueOf(5000.00))
                .dueDate(LocalDate.of(2024, 2, 15))
                .status(InvoiceStatus.DRAFT)
                .description("Monthly service invoice")
                .paidAmount(BigDecimal.ZERO)
                .remainingAmount(BigDecimal.valueOf(5000.00))
                .build();
    }

    @Test
    void createInvoice_Success() {
        // Arrange
        CreateInvoiceRequestDTO requestDTO = CreateInvoiceRequestDTO.builder()
                .partnerId(1L)
                .totalAmount(BigDecimal.valueOf(5000.00))
                .dueDate(LocalDate.of(2024, 2, 15))
                .description("Monthly service invoice")
                .build();

        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(invoiceRepository.countByPartnerIdAndActiveTrue(1L)).thenReturn(0L);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);
        when(invoiceMapper.toResponseDTO(testInvoice)).thenReturn(testInvoiceResponseDTO);

        // Act
        InvoiceResponseDTO result = invoiceService.createInvoice(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testInvoiceResponseDTO.getId(), result.getId());
        assertEquals(testInvoiceResponseDTO.getPartnerId(), result.getPartnerId());
        assertEquals(testInvoiceResponseDTO.getInvoiceNumber(), result.getInvoiceNumber());

        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void createInvoice_PartnerNotFound() {
        // Arrange
        CreateInvoiceRequestDTO requestDTO = CreateInvoiceRequestDTO.builder()
                .partnerId(999L)
                .totalAmount(BigDecimal.valueOf(5000.00))
                .dueDate(LocalDate.of(2024, 2, 15))
                .description("Monthly service invoice")
                .build();

        when(partnerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                invoiceService.createInvoice(requestDTO));

        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void updateInvoice_Success() {
        // Arrange
        Long invoiceId = 1L;
        UpdateInvoiceRequestDTO requestDTO = UpdateInvoiceRequestDTO.builder()
                .totalAmount(BigDecimal.valueOf(5500.00))
                .status(InvoiceStatus.SENT)
                .description("Updated invoice description")
                .build();

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(testInvoice)).thenReturn(testInvoice);
        when(invoiceMapper.toResponseDTO(testInvoice)).thenReturn(testInvoiceResponseDTO);

        // Act
        InvoiceResponseDTO result = invoiceService.updateInvoice(invoiceId, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(5500.00), testInvoice.getTotalAmount());
        assertEquals(InvoiceStatus.SENT, testInvoice.getStatus());
        assertEquals("Updated invoice description", testInvoice.getDescription());

        verify(invoiceRepository).save(testInvoice);
    }

    @Test
    void updateInvoice_NotFound() {
        // Arrange
        Long invoiceId = 999L;
        UpdateInvoiceRequestDTO requestDTO = UpdateInvoiceRequestDTO.builder()
                .totalAmount(BigDecimal.valueOf(5500.00))
                .build();

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                invoiceService.updateInvoice(invoiceId, requestDTO));

        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void markInvoiceAsPaid_Success() {
        // Arrange
        Long invoiceId = 1L;
        BigDecimal amount = BigDecimal.valueOf(5000.00);
        String paymentReference = "PAY_REF_001";

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(testInvoice)).thenReturn(testInvoice);
        when(invoiceMapper.toResponseDTO(testInvoice)).thenReturn(testInvoiceResponseDTO);

        // Act
        InvoiceResponseDTO result = invoiceService.markInvoiceAsPaid(invoiceId, amount, paymentReference);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(5000.00), testInvoice.getPaidAmount());
        assertEquals(paymentReference, testInvoice.getPaymentReference());
        assertEquals(InvoiceStatus.PAID, testInvoice.getStatus());

        verify(invoiceRepository).save(testInvoice);
    }

    @Test
    void markInvoiceAsPaid_PartialPayment() {
        // Arrange
        Long invoiceId = 1L;
        BigDecimal amount = BigDecimal.valueOf(2500.00);
        String paymentReference = "PAY_REF_002";

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(testInvoice)).thenReturn(testInvoice);
        when(invoiceMapper.toResponseDTO(testInvoice)).thenReturn(testInvoiceResponseDTO);

        // Act
        InvoiceResponseDTO result = invoiceService.markInvoiceAsPaid(invoiceId, amount, paymentReference);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(2500.00), testInvoice.getPaidAmount());
        assertEquals(paymentReference, testInvoice.getPaymentReference());
        assertEquals(InvoiceStatus.DRAFT, testInvoice.getStatus()); // Still draft as not fully paid

        verify(invoiceRepository).save(testInvoice);
    }

    @Test
    void markInvoiceAsPaid_NotFound() {
        // Arrange
        Long invoiceId = 999L;
        BigDecimal amount = BigDecimal.valueOf(5000.00);
        String paymentReference = "PAY_REF_001";

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                invoiceService.markInvoiceAsPaid(invoiceId, amount, paymentReference));

        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void sendInvoice_Success() {
        // Arrange
        Long invoiceId = 1L;

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(testInvoice)).thenReturn(testInvoice);
        when(invoiceMapper.toResponseDTO(testInvoice)).thenReturn(testInvoiceResponseDTO);

        // Act
        InvoiceResponseDTO result = invoiceService.sendInvoice(invoiceId);

        // Assert
        assertNotNull(result);
        assertEquals(InvoiceStatus.SENT, testInvoice.getStatus());

        verify(invoiceRepository).save(testInvoice);
    }

    @Test
    void sendInvoice_NotFound() {
        // Arrange
        Long invoiceId = 999L;

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                invoiceService.sendInvoice(invoiceId));

        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void getInvoiceById_Success() {
        // Arrange
        Long invoiceId = 1L;

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(testInvoice));
        when(invoiceMapper.toResponseDTO(testInvoice)).thenReturn(testInvoiceResponseDTO);

        // Act
        InvoiceResponseDTO result = invoiceService.getInvoiceById(invoiceId);

        // Assert
        assertNotNull(result);
        assertEquals(testInvoiceResponseDTO.getId(), result.getId());
    }

    @Test
    void getInvoiceById_NotFound() {
        // Arrange
        Long invoiceId = 999L;

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                invoiceService.getInvoiceById(invoiceId));
    }

    @Test
    void getInvoiceByNumber_Success() {
        // Arrange
        String invoiceNumber = "INV-20240215-0001-001";

        when(invoiceRepository.findByInvoiceNumberAndActiveTrue(invoiceNumber))
                .thenReturn(Optional.of(testInvoice));
        when(invoiceMapper.toResponseDTO(testInvoice)).thenReturn(testInvoiceResponseDTO);

        // Act
        InvoiceResponseDTO result = invoiceService.getInvoiceByNumber(invoiceNumber);

        // Assert
        assertNotNull(result);
        assertEquals(testInvoiceResponseDTO.getInvoiceNumber(), result.getInvoiceNumber());
    }

    @Test
    void getInvoiceByNumber_NotFound() {
        // Arrange
        String invoiceNumber = "INVALID_NUMBER";

        when(invoiceRepository.findByInvoiceNumberAndActiveTrue(invoiceNumber))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                invoiceService.getInvoiceByNumber(invoiceNumber));
    }

    @Test
    void getInvoicesByPartner_Success() {
        // Arrange
        Long partnerId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<Invoice> invoicePage = new PageImpl<>(List.of(testInvoice));

        when(invoiceRepository.findByPartnerIdAndActiveTrue(partnerId, pageable)).thenReturn(invoicePage);
        when(invoiceMapper.toResponseDTO(testInvoice)).thenReturn(testInvoiceResponseDTO);

        // Act
        Page<InvoiceResponseDTO> result = invoiceService.getInvoicesByPartner(partnerId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testInvoiceResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getInvoicesByStatus_Success() {
        // Arrange
        InvoiceStatus status = InvoiceStatus.DRAFT;
        Pageable pageable = PageRequest.of(0, 20);
        Page<Invoice> invoicePage = new PageImpl<>(List.of(testInvoice));

        when(invoiceRepository.findByStatusAndActiveTrue(status, pageable)).thenReturn(invoicePage);
        when(invoiceMapper.toResponseDTO(testInvoice)).thenReturn(testInvoiceResponseDTO);

        // Act
        Page<InvoiceResponseDTO> result = invoiceService.getInvoicesByStatus(status, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testInvoiceResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getInvoicesByDueDate_Success() {
        // Arrange
        LocalDate dueDate = LocalDate.of(2024, 2, 15);
        List<Invoice> invoices = List.of(testInvoice);

        when(invoiceRepository.findByDueDateAndActiveTrue(dueDate)).thenReturn(invoices);
        when(invoiceMapper.toResponseDTOList(invoices)).thenReturn(List.of(testInvoiceResponseDTO));

        // Act
        List<InvoiceResponseDTO> result = invoiceService.getInvoicesByDueDate(dueDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testInvoiceResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getOverdueInvoices_Success() {
        // Arrange
        List<Invoice> overdueInvoices = List.of(testInvoice);

        when(invoiceRepository.findOverdueInvoices(LocalDate.now())).thenReturn(overdueInvoices);
        when(invoiceMapper.toResponseDTOList(overdueInvoices)).thenReturn(List.of(testInvoiceResponseDTO));

        // Act
        List<InvoiceResponseDTO> result = invoiceService.getOverdueInvoices();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testInvoiceResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getInvoicesDueSoon_Success() {
        // Arrange
        List<Invoice> dueSoonInvoices = List.of(testInvoice);

        when(invoiceRepository.findInvoicesDueSoon(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(dueSoonInvoices);
        when(invoiceMapper.toResponseDTOList(dueSoonInvoices)).thenReturn(List.of(testInvoiceResponseDTO));

        // Act
        List<InvoiceResponseDTO> result = invoiceService.getInvoicesDueSoon();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testInvoiceResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getUnpaidInvoices_Success() {
        // Arrange
        List<Invoice> unpaidInvoices = List.of(testInvoice);

        when(invoiceRepository.findUnpaidInvoices()).thenReturn(unpaidInvoices);
        when(invoiceMapper.toResponseDTOList(unpaidInvoices)).thenReturn(List.of(testInvoiceResponseDTO));

        // Act
        List<InvoiceResponseDTO> result = invoiceService.getUnpaidInvoices();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testInvoiceResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getTotalInvoicesByPartner_Success() {
        // Arrange
        Long partnerId = 1L;
        BigDecimal expectedTotal = BigDecimal.valueOf(15000.00);

        when(invoiceRepository.sumAmountByPartnerId(partnerId)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = invoiceService.getTotalInvoicesByPartner(partnerId);

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void getPaidAmountByPartner_Success() {
        // Arrange
        Long partnerId = 1L;
        BigDecimal expectedPaid = BigDecimal.valueOf(8000.00);

        when(invoiceRepository.sumPaidAmountByPartnerId(partnerId)).thenReturn(expectedPaid);

        // Act
        BigDecimal result = invoiceService.getPaidAmountByPartner(partnerId);

        // Assert
        assertEquals(expectedPaid, result);
    }

    @Test
    void getTotalInvoicesByStatus_Success() {
        // Arrange
        InvoiceStatus status = InvoiceStatus.PAID;
        BigDecimal expectedTotal = BigDecimal.valueOf(10000.00);

        when(invoiceRepository.sumAmountByStatus(status)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = invoiceService.getTotalInvoicesByStatus(status);

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void markOverdueInvoices_Success() {
        // Arrange
        List<Invoice> overdueInvoices = List.of(testInvoice);

        when(invoiceRepository.findOverdueInvoices(LocalDate.now())).thenReturn(overdueInvoices);
        when(invoiceRepository.save(testInvoice)).thenReturn(testInvoice);

        // Act
        invoiceService.markOverdueInvoices();

        // Assert
        verify(invoiceRepository).save(testInvoice);
        assertEquals(InvoiceStatus.OVERDUE, testInvoice.getStatus());
    }

    @Test
    void generateInvoiceFromDeliveries_Success() {
        // Arrange
        Long partnerId = 1L;
        LocalDate dueDate = LocalDate.of(2024, 2, 15);
        String description = "Monthly invoice from deliveries";

        when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(testPartner));
        when(invoiceRepository.countByPartnerIdAndActiveTrue(partnerId)).thenReturn(0L);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);
        when(invoiceMapper.toResponseDTO(testInvoice)).thenReturn(testInvoiceResponseDTO);

        // Act
        InvoiceResponseDTO result = invoiceService.generateInvoiceFromDeliveries(partnerId, dueDate, description);

        // Assert
        assertNotNull(result);
        assertEquals(testInvoiceResponseDTO.getId(), result.getId());
        assertEquals(testInvoiceResponseDTO.getPartnerId(), result.getPartnerId());

        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void generateInvoiceFromDeliveries_PartnerNotFound() {
        // Arrange
        Long partnerId = 999L;
        LocalDate dueDate = LocalDate.of(2024, 2, 15);
        String description = "Monthly invoice from deliveries";

        when(partnerRepository.findById(partnerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                invoiceService.generateInvoiceFromDeliveries(partnerId, dueDate, description));

        verify(invoiceRepository, never()).save(any(Invoice.class));
    }
}
