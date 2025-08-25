package org.driver.driverapp.repository;

import org.driver.driverapp.enums.TransactionType;
import org.driver.driverapp.model.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    // Find by wallet
    Page<WalletTransaction> findByWalletIdAndActiveTrue(Long walletId, Pageable pageable);
    
    List<WalletTransaction> findByWalletIdAndTransactionTypeAndActiveTrue(Long walletId, TransactionType transactionType);
    
    // Find by transaction type
    Page<WalletTransaction> findByTransactionTypeAndActiveTrue(TransactionType transactionType, Pageable pageable);
    
    List<WalletTransaction> findByTransactionTypeAndActiveTrue(TransactionType transactionType);
    
    // Find by reference
    List<WalletTransaction> findByReferenceAndActiveTrue(String reference);
    
    // Find by date range
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.createdAt BETWEEN :startDate AND :endDate AND wt.active = true")
    List<WalletTransaction> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    // Find by wallet and date range
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.wallet.id = :walletId AND wt.createdAt BETWEEN :startDate AND :endDate AND wt.active = true")
    List<WalletTransaction> findByWalletIdAndCreatedAtBetween(@Param("walletId") Long walletId,
                                                            @Param("startDate") Instant startDate,
                                                            @Param("endDate") Instant endDate);
    
    // Find by transaction type and date range
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.transactionType = :transactionType AND wt.createdAt BETWEEN :startDate AND :endDate AND wt.active = true")
    List<WalletTransaction> findByTransactionTypeAndCreatedAtBetween(@Param("transactionType") TransactionType transactionType,
                                                                   @Param("startDate") Instant startDate,
                                                                   @Param("endDate") Instant endDate);
    
    // Count by transaction type
    long countByTransactionTypeAndActiveTrue(TransactionType transactionType);
    
    // Count by wallet
    long countByWalletIdAndActiveTrue(Long walletId);
    
    // Sum amounts by transaction type
    @Query("SELECT COALESCE(SUM(wt.amount), 0) FROM WalletTransaction wt WHERE wt.transactionType = :transactionType AND wt.active = true")
    BigDecimal sumAmountByTransactionType(@Param("transactionType") TransactionType transactionType);
    
    // Sum amounts by wallet
    @Query("SELECT COALESCE(SUM(wt.amount), 0) FROM WalletTransaction wt WHERE wt.wallet.id = :walletId AND wt.active = true")
    BigDecimal sumAmountByWalletId(@Param("walletId") Long walletId);
    
    // Sum amounts by wallet and transaction type
    @Query("SELECT COALESCE(SUM(wt.amount), 0) FROM WalletTransaction wt WHERE wt.wallet.id = :walletId AND wt.transactionType = :transactionType AND wt.active = true")
    BigDecimal sumAmountByWalletIdAndTransactionType(@Param("walletId") Long walletId, @Param("transactionType") TransactionType transactionType);
    
    // Find recent transactions
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.active = true ORDER BY wt.createdAt DESC")
    List<WalletTransaction> findRecentTransactions();
    
    // Find transactions by amount range
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.amount BETWEEN :minAmount AND :maxAmount AND wt.active = true")
    List<WalletTransaction> findByAmountRange(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    // Find credit transactions
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.transactionType = 'CREDIT' AND wt.active = true ORDER BY wt.createdAt DESC")
    List<WalletTransaction> findCreditTransactions();
    
    // Find debit transactions
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.transactionType = 'DEBIT' AND wt.active = true ORDER BY wt.createdAt DESC")
    List<WalletTransaction> findDebitTransactions();
    
    // Find transactions by owner type
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.wallet.ownerType = :ownerType AND wt.active = true ORDER BY wt.createdAt DESC")
    List<WalletTransaction> findByOwnerType(@Param("ownerType") String ownerType);
}
