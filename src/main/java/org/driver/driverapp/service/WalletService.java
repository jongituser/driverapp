package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.wallet.request.CreditWalletRequestDTO;
import org.driver.driverapp.dto.wallet.request.DebitWalletRequestDTO;
import org.driver.driverapp.dto.wallet.response.WalletResponseDTO;
import org.driver.driverapp.dto.wallet.response.WalletTransactionResponseDTO;
import org.driver.driverapp.enums.TransactionType;
import org.driver.driverapp.enums.WalletOwnerType;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.WalletMapper;
import org.driver.driverapp.mapper.WalletTransactionMapper;
import org.driver.driverapp.model.Wallet;
import org.driver.driverapp.model.WalletTransaction;
import org.driver.driverapp.repository.WalletRepository;
import org.driver.driverapp.repository.WalletTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletMapper walletMapper;
    private final WalletTransactionMapper walletTransactionMapper;

    @Transactional
    public WalletResponseDTO createWallet(Long ownerId, WalletOwnerType ownerType, String description) {
        log.info("Creating wallet for owner: {}, type: {}", ownerId, ownerType);

        // Check if wallet already exists
        Optional<Wallet> existingWallet = walletRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(ownerId, ownerType);
        if (existingWallet.isPresent()) {
            throw new IllegalStateException("Wallet already exists for owner: " + ownerId + " and type: " + ownerType);
        }

        // Create wallet
        Wallet wallet = Wallet.builder()
                .ownerId(ownerId)
                .ownerType(ownerType)
                .balance(BigDecimal.ZERO)
                .description(description)
                .active(true)
                .build();

        wallet = walletRepository.save(wallet);
        log.info("Wallet created successfully: {}", wallet.getId());

        return walletMapper.toResponseDTO(wallet);
    }

    @Transactional
    public WalletResponseDTO creditWallet(CreditWalletRequestDTO requestDTO) {
        log.info("Crediting wallet for owner: {}, type: {}, amount: {}", 
                requestDTO.getOwnerId(), requestDTO.getOwnerType(), requestDTO.getAmount());

        // Get or create wallet
        Wallet wallet = getOrCreateWallet(requestDTO.getOwnerId(), requestDTO.getOwnerType());

        // Record balance before transaction
        BigDecimal balanceBefore = wallet.getBalance();

        // Credit the wallet
        wallet.credit(requestDTO.getAmount());
        wallet = walletRepository.save(wallet);

        // Create transaction record
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .transactionType(TransactionType.CREDIT)
                .amount(requestDTO.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(wallet.getBalance())
                .reference(requestDTO.getReference())
                .description(requestDTO.getDescription())
                .metadata(requestDTO.getMetadata())
                .active(true)
                .build();

        walletTransactionRepository.save(transaction);

        log.info("Wallet credited successfully: {}", wallet.getId());
        return walletMapper.toResponseDTO(wallet);
    }

    @Transactional
    public WalletResponseDTO debitWallet(DebitWalletRequestDTO requestDTO) {
        log.info("Debiting wallet for owner: {}, type: {}, amount: {}", 
                requestDTO.getOwnerId(), requestDTO.getOwnerType(), requestDTO.getAmount());

        // Get or create wallet
        Wallet wallet = getOrCreateWallet(requestDTO.getOwnerId(), requestDTO.getOwnerType());

        // Check if sufficient balance
        if (!wallet.hasSufficientBalance(requestDTO.getAmount())) {
            throw new IllegalStateException("Insufficient balance. Available: " + wallet.getBalance() + 
                    ", Required: " + requestDTO.getAmount());
        }

        // Record balance before transaction
        BigDecimal balanceBefore = wallet.getBalance();

        // Debit the wallet
        wallet.debit(requestDTO.getAmount());
        wallet = walletRepository.save(wallet);

        // Create transaction record
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .transactionType(TransactionType.DEBIT)
                .amount(requestDTO.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(wallet.getBalance())
                .reference(requestDTO.getReference())
                .description(requestDTO.getDescription())
                .metadata(requestDTO.getMetadata())
                .active(true)
                .build();

        walletTransactionRepository.save(transaction);

        log.info("Wallet debited successfully: {}", wallet.getId());
        return walletMapper.toResponseDTO(wallet);
    }

    @Transactional(readOnly = true)
    public WalletResponseDTO getWalletById(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + id));
        return walletMapper.toResponseDTO(wallet);
    }

    @Transactional(readOnly = true)
    public WalletResponseDTO getWalletByOwner(Long ownerId, WalletOwnerType ownerType) {
        Wallet wallet = walletRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(ownerId, ownerType)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for owner: " + ownerId + 
                        " and type: " + ownerType));
        return walletMapper.toResponseDTO(wallet);
    }

    @Transactional(readOnly = true)
    public List<WalletResponseDTO> getWalletsByOwner(Long ownerId) {
        List<Wallet> wallets = walletRepository.findByOwnerIdAndActiveTrue(ownerId);
        return walletMapper.toResponseDTOList(wallets);
    }

    @Transactional(readOnly = true)
    public List<WalletResponseDTO> getWalletsByType(WalletOwnerType ownerType) {
        List<Wallet> wallets = walletRepository.findByOwnerTypeAndActiveTrue(ownerType);
        return walletMapper.toResponseDTOList(wallets);
    }

    @Transactional(readOnly = true)
    public Page<WalletTransactionResponseDTO> getWalletTransactions(Long walletId, Pageable pageable) {
        Page<WalletTransaction> transactions = walletTransactionRepository.findByWalletIdAndActiveTrue(walletId, pageable);
        return transactions.map(walletTransactionMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<WalletTransactionResponseDTO> getWalletTransactionsByType(Long walletId, TransactionType transactionType) {
        List<WalletTransaction> transactions = walletTransactionRepository.findByWalletIdAndTransactionTypeAndActiveTrue(walletId, transactionType);
        return walletTransactionMapper.toResponseDTOList(transactions);
    }

    @Transactional(readOnly = true)
    public List<WalletTransactionResponseDTO> getRecentTransactions() {
        List<WalletTransaction> transactions = walletTransactionRepository.findRecentTransactions();
        return walletTransactionMapper.toResponseDTOList(transactions);
    }

    @Transactional(readOnly = true)
    public List<WalletTransactionResponseDTO> getCreditTransactions() {
        List<WalletTransaction> transactions = walletTransactionRepository.findCreditTransactions();
        return walletTransactionMapper.toResponseDTOList(transactions);
    }

    @Transactional(readOnly = true)
    public List<WalletTransactionResponseDTO> getDebitTransactions() {
        List<WalletTransaction> transactions = walletTransactionRepository.findDebitTransactions();
        return walletTransactionMapper.toResponseDTOList(transactions);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance() {
        return walletRepository.sumTotalBalance();
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalanceByType(WalletOwnerType ownerType) {
        return walletRepository.sumBalanceByOwnerType(ownerType);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCredits() {
        return walletTransactionRepository.sumAmountByTransactionType(TransactionType.CREDIT);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalDebits() {
        return walletTransactionRepository.sumAmountByTransactionType(TransactionType.DEBIT);
    }

    @Transactional(readOnly = true)
    public List<WalletResponseDTO> getWalletsWithLowBalance(BigDecimal threshold) {
        List<Wallet> wallets = walletRepository.findWalletsWithLowBalance(threshold);
        return walletMapper.toResponseDTOList(wallets);
    }

    @Transactional(readOnly = true)
    public List<WalletResponseDTO> getWalletsWithHighBalance(BigDecimal threshold) {
        List<Wallet> wallets = walletRepository.findWalletsWithHighBalance(threshold);
        return walletMapper.toResponseDTOList(wallets);
    }

    @Transactional(readOnly = true)
    public List<WalletResponseDTO> getTopWalletsByBalance() {
        List<Wallet> wallets = walletRepository.findTopWalletsByBalance();
        return walletMapper.toResponseDTOList(wallets);
    }

    @Transactional(readOnly = true)
    public List<WalletResponseDTO> getDriverWallets() {
        List<Wallet> wallets = walletRepository.findDriverWallets();
        return walletMapper.toResponseDTOList(wallets);
    }

    @Transactional(readOnly = true)
    public List<WalletResponseDTO> getPartnerWallets() {
        List<Wallet> wallets = walletRepository.findPartnerWallets();
        return walletMapper.toResponseDTOList(wallets);
    }

    private Wallet getOrCreateWallet(Long ownerId, WalletOwnerType ownerType) {
        return walletRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(ownerId, ownerType)
                .orElseGet(() -> {
                    log.info("Creating new wallet for owner: {}, type: {}", ownerId, ownerType);
                    Wallet newWallet = Wallet.builder()
                            .ownerId(ownerId)
                            .ownerType(ownerType)
                            .balance(BigDecimal.ZERO)
                            .active(true)
                            .build();
                    return walletRepository.save(newWallet);
                });
    }
}
