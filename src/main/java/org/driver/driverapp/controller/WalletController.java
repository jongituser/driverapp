package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.wallet.request.CreditWalletRequestDTO;
import org.driver.driverapp.dto.wallet.request.DebitWalletRequestDTO;
import org.driver.driverapp.dto.wallet.response.WalletResponseDTO;
import org.driver.driverapp.dto.wallet.response.WalletTransactionResponseDTO;
import org.driver.driverapp.enums.TransactionType;
import org.driver.driverapp.enums.WalletOwnerType;
import org.driver.driverapp.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<WalletResponseDTO> createWallet(
            @RequestParam Long ownerId,
            @RequestParam WalletOwnerType ownerType,
            @RequestParam(required = false) String description) {
        log.info("Creating wallet for owner: {}, type: {}", ownerId, ownerType);
        
        WalletResponseDTO response = walletService.createWallet(ownerId, ownerType, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/credit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<WalletResponseDTO> creditWallet(@Valid @RequestBody CreditWalletRequestDTO requestDTO) {
        log.info("Crediting wallet for owner: {}, type: {}", requestDTO.getOwnerId(), requestDTO.getOwnerType());
        
        WalletResponseDTO response = walletService.creditWallet(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/debit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<WalletResponseDTO> debitWallet(@Valid @RequestBody DebitWalletRequestDTO requestDTO) {
        log.info("Debiting wallet for owner: {}, type: {}", requestDTO.getOwnerId(), requestDTO.getOwnerType());
        
        WalletResponseDTO response = walletService.debitWallet(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<WalletResponseDTO> getWalletById(@PathVariable Long id) {
        WalletResponseDTO response = walletService.getWalletById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner/{ownerId}/type/{ownerType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<WalletResponseDTO> getWalletByOwner(
            @PathVariable Long ownerId,
            @PathVariable WalletOwnerType ownerType) {
        WalletResponseDTO response = walletService.getWalletByOwner(ownerId, ownerType);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<WalletResponseDTO>> getWalletsByOwner(@PathVariable Long ownerId) {
        List<WalletResponseDTO> response = walletService.getWalletsByOwner(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{ownerType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<WalletResponseDTO>> getWalletsByType(@PathVariable WalletOwnerType ownerType) {
        List<WalletResponseDTO> response = walletService.getWalletsByType(ownerType);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{walletId}/transactions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<WalletTransactionResponseDTO>> getWalletTransactions(
            @PathVariable Long walletId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<WalletTransactionResponseDTO> response = walletService.getWalletTransactions(walletId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{walletId}/transactions/{transactionType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<WalletTransactionResponseDTO>> getWalletTransactionsByType(
            @PathVariable Long walletId,
            @PathVariable TransactionType transactionType) {
        List<WalletTransactionResponseDTO> response = walletService.getWalletTransactionsByType(walletId, transactionType);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/recent")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<WalletTransactionResponseDTO>> getRecentTransactions() {
        List<WalletTransactionResponseDTO> response = walletService.getRecentTransactions();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/credits")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<WalletTransactionResponseDTO>> getCreditTransactions() {
        List<WalletTransactionResponseDTO> response = walletService.getCreditTransactions();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/debits")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<WalletTransactionResponseDTO>> getDebitTransactions() {
        List<WalletTransactionResponseDTO> response = walletService.getDebitTransactions();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getTotalBalance() {
        BigDecimal total = walletService.getTotalBalance();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/balance/type/{ownerType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getTotalBalanceByType(@PathVariable WalletOwnerType ownerType) {
        BigDecimal total = walletService.getTotalBalanceByType(ownerType);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/transactions/total/credits")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getTotalCredits() {
        BigDecimal total = walletService.getTotalCredits();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/transactions/total/debits")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getTotalDebits() {
        BigDecimal total = walletService.getTotalDebits();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/low-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<WalletResponseDTO>> getWalletsWithLowBalance(@RequestParam BigDecimal threshold) {
        List<WalletResponseDTO> response = walletService.getWalletsWithLowBalance(threshold);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/high-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<WalletResponseDTO>> getWalletsWithHighBalance(@RequestParam BigDecimal threshold) {
        List<WalletResponseDTO> response = walletService.getWalletsWithHighBalance(threshold);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<WalletResponseDTO>> getTopWalletsByBalance() {
        List<WalletResponseDTO> response = walletService.getTopWalletsByBalance();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/drivers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<WalletResponseDTO>> getDriverWallets() {
        List<WalletResponseDTO> response = walletService.getDriverWallets();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/partners")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<WalletResponseDTO>> getPartnerWallets() {
        List<WalletResponseDTO> response = walletService.getPartnerWallets();
        return ResponseEntity.ok(response);
    }
}
