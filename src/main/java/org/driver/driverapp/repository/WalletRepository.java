package org.driver.driverapp.repository;

import org.driver.driverapp.enums.WalletOwnerType;
import org.driver.driverapp.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // Find by owner
    Optional<Wallet> findByOwnerIdAndOwnerTypeAndActiveTrue(Long ownerId, WalletOwnerType ownerType);
    
    List<Wallet> findByOwnerIdAndActiveTrue(Long ownerId);
    
    List<Wallet> findByOwnerTypeAndActiveTrue(WalletOwnerType ownerType);
    
    // Find by balance range
    @Query("SELECT w FROM Wallet w WHERE w.balance BETWEEN :minBalance AND :maxBalance AND w.active = true")
    List<Wallet> findByBalanceRange(@Param("minBalance") BigDecimal minBalance, @Param("maxBalance") BigDecimal maxBalance);
    
    // Find wallets with low balance
    @Query("SELECT w FROM Wallet w WHERE w.balance < :threshold AND w.active = true")
    List<Wallet> findWalletsWithLowBalance(@Param("threshold") BigDecimal threshold);
    
    // Find wallets with high balance
    @Query("SELECT w FROM Wallet w WHERE w.balance > :threshold AND w.active = true")
    List<Wallet> findWalletsWithHighBalance(@Param("threshold") BigDecimal threshold);
    
    // Count by owner type
    long countByOwnerTypeAndActiveTrue(WalletOwnerType ownerType);
    
    // Sum total balance by owner type
    @Query("SELECT COALESCE(SUM(w.balance), 0) FROM Wallet w WHERE w.ownerType = :ownerType AND w.active = true")
    BigDecimal sumBalanceByOwnerType(@Param("ownerType") WalletOwnerType ownerType);
    
    // Sum total balance
    @Query("SELECT COALESCE(SUM(w.balance), 0) FROM Wallet w WHERE w.active = true")
    BigDecimal sumTotalBalance();
    
    // Find top wallets by balance
    @Query("SELECT w FROM Wallet w WHERE w.active = true ORDER BY w.balance DESC")
    List<Wallet> findTopWalletsByBalance();
    
    // Find driver wallets
    @Query("SELECT w FROM Wallet w WHERE w.ownerType = 'DRIVER' AND w.active = true")
    List<Wallet> findDriverWallets();
    
    // Find partner wallets
    @Query("SELECT w FROM Wallet w WHERE w.ownerType = 'PARTNER' AND w.active = true")
    List<Wallet> findPartnerWallets();
}
