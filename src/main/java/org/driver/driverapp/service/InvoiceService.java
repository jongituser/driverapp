package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PartnerRepository partnerRepository;
    private final InvoiceMapper invoiceMapper;

    @Transactional
    public InvoiceResponseDTO createInvoice(CreateInvoiceRequestDTO requestDTO) {
        log.info("Creating invoice for partner: {}, amount: {}", requestDTO.getPartnerId(), requestDTO.getTotalAmount());

        // Validate partner
        Partner partner = partnerRepository.findById(requestDTO.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + requestDTO.getPartnerId()));

        // Generate invoice number
        String invoiceNumber = generateInvoiceNumber(partner.getId());

        // Create invoice
        Invoice invoice = Invoice.builder()
                .partner(partner)
                .invoiceNumber(invoiceNumber)
                .totalAmount(requestDTO.getTotalAmount())
                .dueDate(requestDTO.getDueDate())
                .status(InvoiceStatus.DRAFT)
                .description(requestDTO.getDescription())
                .active(true)
                .build();

        invoice = invoiceRepository.save(invoice);
        log.info("Invoice created successfully: {}", invoice.getId());

        return invoiceMapper.toResponseDTO(invoice);
    }

    @Transactional
    public InvoiceResponseDTO updateInvoice(Long id, UpdateInvoiceRequestDTO requestDTO) {
        log.info("Updating invoice: {}", id);

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (requestDTO.getTotalAmount() != null) {
            invoice.setTotalAmount(requestDTO.getTotalAmount());
        }
        if (requestDTO.getDueDate() != null) {
            invoice.setDueDate(requestDTO.getDueDate());
        }
        if (requestDTO.getStatus() != null) {
            invoice.setStatus(requestDTO.getStatus());
        }
        if (requestDTO.getDescription() != null) {
            invoice.setDescription(requestDTO.getDescription());
        }

        invoice = invoiceRepository.save(invoice);
        log.info("Invoice updated successfully: {}", id);

        return invoiceMapper.toResponseDTO(invoice);
    }

    @Transactional
    public InvoiceResponseDTO markInvoiceAsPaid(Long id, BigDecimal amount, String paymentReference) {
        log.info("Marking invoice as paid: {}, amount: {}", id, amount);

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        invoice.markAsPaid(amount, paymentReference);
        invoice = invoiceRepository.save(invoice);

        log.info("Invoice marked as paid: {}", id);
        return invoiceMapper.toResponseDTO(invoice);
    }

    @Transactional
    public InvoiceResponseDTO sendInvoice(Long id) {
        log.info("Sending invoice: {}", id);

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        invoice.markAsSent();
        invoice = invoiceRepository.save(invoice);

        log.info("Invoice sent: {}", id);
        return invoiceMapper.toResponseDTO(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return invoiceMapper.toResponseDTO(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumberAndActiveTrue(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with number: " + invoiceNumber));
        return invoiceMapper.toResponseDTO(invoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponseDTO> getInvoicesByPartner(Long partnerId, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findByPartnerIdAndActiveTrue(partnerId, pageable);
        return invoices.map(invoiceMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponseDTO> getInvoicesByStatus(InvoiceStatus status, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findByStatusAndActiveTrue(status, pageable);
        return invoices.map(invoiceMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getInvoicesByDueDate(LocalDate dueDate) {
        List<Invoice> invoices = invoiceRepository.findByDueDateAndActiveTrue(dueDate);
        return invoiceMapper.toResponseDTOList(invoices);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getOverdueInvoices() {
        List<Invoice> invoices = invoiceRepository.findOverdueInvoices(LocalDate.now());
        return invoiceMapper.toResponseDTOList(invoices);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getInvoicesDueSoon() {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        List<Invoice> invoices = invoiceRepository.findInvoicesDueSoon(today, nextWeek);
        return invoiceMapper.toResponseDTOList(invoices);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getUnpaidInvoices() {
        List<Invoice> invoices = invoiceRepository.findUnpaidInvoices();
        return invoiceMapper.toResponseDTOList(invoices);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalInvoicesByPartner(Long partnerId) {
        return invoiceRepository.sumAmountByPartnerId(partnerId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getPaidAmountByPartner(Long partnerId) {
        return invoiceRepository.sumPaidAmountByPartnerId(partnerId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.sumAmountByStatus(status);
    }

    @Transactional
    public void markOverdueInvoices() {
        log.info("Marking overdue invoices");
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDate.now());
        
        for (Invoice invoice : overdueInvoices) {
            invoice.markAsOverdue();
            invoiceRepository.save(invoice);
        }
        
        log.info("Marked {} invoices as overdue", overdueInvoices.size());
    }

    @Transactional
    public InvoiceResponseDTO generateInvoiceFromDeliveries(Long partnerId, LocalDate dueDate, String description) {
        log.info("Generating invoice from deliveries for partner: {}", partnerId);

        // This would typically calculate the total from completed deliveries
        // For now, we'll create a placeholder invoice
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + partnerId));

        String invoiceNumber = generateInvoiceNumber(partnerId);
        
        // TODO: Calculate actual amount from deliveries
        BigDecimal totalAmount = BigDecimal.valueOf(1000.00); // Placeholder

        Invoice invoice = Invoice.builder()
                .partner(partner)
                .invoiceNumber(invoiceNumber)
                .totalAmount(totalAmount)
                .dueDate(dueDate)
                .status(InvoiceStatus.DRAFT)
                .description(description)
                .active(true)
                .build();

        invoice = invoiceRepository.save(invoice);
        log.info("Invoice generated from deliveries: {}", invoice.getId());

        return invoiceMapper.toResponseDTO(invoice);
    }

    private String generateInvoiceNumber(Long partnerId) {
        LocalDate today = LocalDate.now();
        String datePrefix = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String partnerPrefix = String.format("%04d", partnerId);
        
        // Get count of invoices for this partner today
        long count = invoiceRepository.countByPartnerIdAndActiveTrue(partnerId);
        
        return String.format("INV-%s-%s-%03d", datePrefix, partnerPrefix, count + 1);
    }
}

