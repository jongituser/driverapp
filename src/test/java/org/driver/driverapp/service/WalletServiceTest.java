package org.driver.driverapp.service;

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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletTransactionRepository walletTransactionRepository;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private WalletTransactionMapper walletTransactionMapper;

    @InjectMocks
    private WalletService walletService;

    private Wallet testWallet;
    private WalletTransaction testTransaction;
    private WalletResponseDTO testWalletResponseDTO;
    private WalletTransactionResponseDTO testTransactionResponseDTO;

    @BeforeEach
    void setUp() {
        testWallet = Wallet.builder()
                .id(1L)
                .ownerId(1L)
                .ownerType(WalletOwnerType.DRIVER)
                .balance(BigDecimal.valueOf(1000.00))
                .description("Test driver wallet")
                .active(true)
                .build();

        testTransaction = WalletTransaction.builder()
                .id(1L)
                .wallet(testWallet)
                .transactionType(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(500.00))
                .balanceBefore(BigDecimal.valueOf(1000.00))
                .balanceAfter(BigDecimal.valueOf(1500.00))
                .reference("TEST_REF_001")
                .description("Test credit transaction")
                .active(true)
                .build();

        testWalletResponseDTO = WalletResponseDTO.builder()
                .id(1L)
                .ownerId(1L)
                .ownerType(WalletOwnerType.DRIVER)
                .balance(BigDecimal.valueOf(1000.00))
                .description("Test driver wallet")
                .build();

        testTransactionResponseDTO = WalletTransactionResponseDTO.builder()
                .id(1L)
                .walletId(1L)
                .transactionType(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(500.00))
                .balanceBefore(BigDecimal.valueOf(1000.00))
                .balanceAfter(BigDecimal.valueOf(1500.00))
                .reference("TEST_REF_001")
                .description("Test credit transaction")
                .build();
    }

    @Test
    void createWallet_Success() {
        // Arrange
        Long ownerId = 1L;
        WalletOwnerType ownerType = WalletOwnerType.DRIVER;
        String description = "New driver wallet";

        when(walletRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(ownerId, ownerType))
                .thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);
        when(walletMapper.toResponseDTO(testWallet)).thenReturn(testWalletResponseDTO);

        // Act
        WalletResponseDTO result = walletService.createWallet(ownerId, ownerType, description);

        // Assert
        assertNotNull(result);
        assertEquals(testWalletResponseDTO.getId(), result.getId());
        assertEquals(testWalletResponseDTO.getOwnerId(), result.getOwnerId());
        assertEquals(testWalletResponseDTO.getOwnerType(), result.getOwnerType());

        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWallet_AlreadyExists() {
        // Arrange
        Long ownerId = 1L;
        WalletOwnerType ownerType = WalletOwnerType.DRIVER;
        String description = "New driver wallet";

        when(walletRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(ownerId, ownerType))
                .thenReturn(Optional.of(testWallet));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                walletService.createWallet(ownerId, ownerType, description));

        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void creditWallet_Success() {
        // Arrange
        CreditWalletRequestDTO requestDTO = CreditWalletRequestDTO.builder()
                .ownerId(1L)
                .ownerType(WalletOwnerType.DRIVER)
                .amount(BigDecimal.valueOf(500.00))
                .reference("CREDIT_REF_001")
                .description("Test credit")
                .build();

        when(walletRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(1L, WalletOwnerType.DRIVER))
                .thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);
        when(walletTransactionRepository.save(any(WalletTransaction.class))).thenReturn(testTransaction);
        when(walletMapper.toResponseDTO(testWallet)).thenReturn(testWalletResponseDTO);

        // Act
        WalletResponseDTO result = walletService.creditWallet(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1500.00), testWallet.getBalance());

        verify(walletRepository).save(testWallet);
        verify(walletTransactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    void creditWallet_CreateNewWallet() {
        // Arrange
        CreditWalletRequestDTO requestDTO = CreditWalletRequestDTO.builder()
                .ownerId(2L)
                .ownerType(WalletOwnerType.PARTNER)
                .amount(BigDecimal.valueOf(1000.00))
                .reference("CREDIT_REF_002")
                .description("Test credit for new wallet")
                .build();

        Wallet newWallet = Wallet.builder()
                .id(2L)
                .ownerId(2L)
                .ownerType(WalletOwnerType.PARTNER)
                .balance(BigDecimal.valueOf(1000.00))
                .active(true)
                .build();

        when(walletRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(2L, WalletOwnerType.PARTNER))
                .thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenReturn(newWallet);
        when(walletTransactionRepository.save(any(WalletTransaction.class))).thenReturn(testTransaction);

        WalletResponseDTO newWalletResponseDTO = WalletResponseDTO.builder()
                .id(2L)
                .ownerId(2L)
                .ownerType(WalletOwnerType.PARTNER)
                .balance(BigDecimal.valueOf(1000.00))
                .build();

        when(walletMapper.toResponseDTO(newWallet)).thenReturn(newWalletResponseDTO);

        // Act
        WalletResponseDTO result = walletService.creditWallet(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(BigDecimal.valueOf(1000.00), result.getBalance());

        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(walletTransactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    void debitWallet_Success() {
        // Arrange
        DebitWalletRequestDTO requestDTO = DebitWalletRequestDTO.builder()
                .ownerId(1L)
                .ownerType(WalletOwnerType.DRIVER)
                .amount(BigDecimal.valueOf(200.00))
                .reference("DEBIT_REF_001")
                .description("Test debit")
                .build();

        when(walletRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(1L, WalletOwnerType.DRIVER))
                .thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);
        when(walletTransactionRepository.save(any(WalletTransaction.class))).thenReturn(testTransaction);
        when(walletMapper.toResponseDTO(testWallet)).thenReturn(testWalletResponseDTO);

        // Act
        WalletResponseDTO result = walletService.debitWallet(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(800.00), testWallet.getBalance());

        verify(walletRepository).save(testWallet);
        verify(walletTransactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    void debitWallet_InsufficientBalance() {
        // Arrange
        DebitWalletRequestDTO requestDTO = DebitWalletRequestDTO.builder()
                .ownerId(1L)
                .ownerType(WalletOwnerType.DRIVER)
                .amount(BigDecimal.valueOf(2000.00))
                .reference("DEBIT_REF_002")
                .description("Test debit with insufficient balance")
                .build();

        when(walletRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(1L, WalletOwnerType.DRIVER))
                .thenReturn(Optional.of(testWallet));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                walletService.debitWallet(requestDTO));

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(walletTransactionRepository, never()).save(any(WalletTransaction.class));
    }

    @Test
    void getWalletById_Success() {
        // Arrange
        Long walletId = 1L;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(testWallet));
        when(walletMapper.toResponseDTO(testWallet)).thenReturn(testWalletResponseDTO);

        // Act
        WalletResponseDTO result = walletService.getWalletById(walletId);

        // Assert
        assertNotNull(result);
        assertEquals(testWalletResponseDTO.getId(), result.getId());
    }

    @Test
    void getWalletById_NotFound() {
        // Arrange
        Long walletId = 999L;

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                walletService.getWalletById(walletId));
    }

    @Test
    void getWalletByOwner_Success() {
        // Arrange
        Long ownerId = 1L;
        WalletOwnerType ownerType = WalletOwnerType.DRIVER;

        when(walletRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(ownerId, ownerType))
                .thenReturn(Optional.of(testWallet));
        when(walletMapper.toResponseDTO(testWallet)).thenReturn(testWalletResponseDTO);

        // Act
        WalletResponseDTO result = walletService.getWalletByOwner(ownerId, ownerType);

        // Assert
        assertNotNull(result);
        assertEquals(testWalletResponseDTO.getId(), result.getId());
    }

    @Test
    void getWalletByOwner_NotFound() {
        // Arrange
        Long ownerId = 999L;
        WalletOwnerType ownerType = WalletOwnerType.DRIVER;

        when(walletRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(ownerId, ownerType))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                walletService.getWalletByOwner(ownerId, ownerType));
    }

    @Test
    void getWalletsByOwner_Success() {
        // Arrange
        Long ownerId = 1L;
        List<Wallet> wallets = List.of(testWallet);

        when(walletRepository.findByOwnerIdAndActiveTrue(ownerId)).thenReturn(wallets);
        when(walletMapper.toResponseDTOList(wallets)).thenReturn(List.of(testWalletResponseDTO));

        // Act
        List<WalletResponseDTO> result = walletService.getWalletsByOwner(ownerId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testWalletResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getWalletsByType_Success() {
        // Arrange
        WalletOwnerType ownerType = WalletOwnerType.DRIVER;
        List<Wallet> wallets = List.of(testWallet);

        when(walletRepository.findByOwnerTypeAndActiveTrue(ownerType)).thenReturn(wallets);
        when(walletMapper.toResponseDTOList(wallets)).thenReturn(List.of(testWalletResponseDTO));

        // Act
        List<WalletResponseDTO> result = walletService.getWalletsByType(ownerType);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testWalletResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getWalletTransactions_Success() {
        // Arrange
        Long walletId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<WalletTransaction> transactionPage = new PageImpl<>(List.of(testTransaction));

        when(walletTransactionRepository.findByWalletIdAndActiveTrue(walletId, pageable))
                .thenReturn(transactionPage);
        when(walletTransactionMapper.toResponseDTO(testTransaction)).thenReturn(testTransactionResponseDTO);

        // Act
        Page<WalletTransactionResponseDTO> result = walletService.getWalletTransactions(walletId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testTransactionResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getWalletTransactionsByType_Success() {
        // Arrange
        Long walletId = 1L;
        TransactionType transactionType = TransactionType.CREDIT;
        List<WalletTransaction> transactions = List.of(testTransaction);

        when(walletTransactionRepository.findByWalletIdAndTransactionTypeAndActiveTrue(walletId, transactionType))
                .thenReturn(transactions);
        when(walletTransactionMapper.toResponseDTOList(transactions)).thenReturn(List.of(testTransactionResponseDTO));

        // Act
        List<WalletTransactionResponseDTO> result = walletService.getWalletTransactionsByType(walletId, transactionType);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTransactionResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getRecentTransactions_Success() {
        // Arrange
        List<WalletTransaction> transactions = List.of(testTransaction);

        when(walletTransactionRepository.findRecentTransactions()).thenReturn(transactions);
        when(walletTransactionMapper.toResponseDTOList(transactions)).thenReturn(List.of(testTransactionResponseDTO));

        // Act
        List<WalletTransactionResponseDTO> result = walletService.getRecentTransactions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTransactionResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getCreditTransactions_Success() {
        // Arrange
        List<WalletTransaction> transactions = List.of(testTransaction);

        when(walletTransactionRepository.findCreditTransactions()).thenReturn(transactions);
        when(walletTransactionMapper.toResponseDTOList(transactions)).thenReturn(List.of(testTransactionResponseDTO));

        // Act
        List<WalletTransactionResponseDTO> result = walletService.getCreditTransactions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTransactionResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getDebitTransactions_Success() {
        // Arrange
        List<WalletTransaction> transactions = List.of(testTransaction);

        when(walletTransactionRepository.findDebitTransactions()).thenReturn(transactions);
        when(walletTransactionMapper.toResponseDTOList(transactions)).thenReturn(List.of(testTransactionResponseDTO));

        // Act
        List<WalletTransactionResponseDTO> result = walletService.getDebitTransactions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTransactionResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getTotalBalance_Success() {
        // Arrange
        BigDecimal expectedTotal = BigDecimal.valueOf(5000.00);

        when(walletRepository.sumTotalBalance()).thenReturn(expectedTotal);

        // Act
        BigDecimal result = walletService.getTotalBalance();

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void getTotalBalanceByType_Success() {
        // Arrange
        WalletOwnerType ownerType = WalletOwnerType.DRIVER;
        BigDecimal expectedTotal = BigDecimal.valueOf(2000.00);

        when(walletRepository.sumBalanceByOwnerType(ownerType)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = walletService.getTotalBalanceByType(ownerType);

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void getTotalCredits_Success() {
        // Arrange
        BigDecimal expectedTotal = BigDecimal.valueOf(3000.00);

        when(walletTransactionRepository.sumAmountByTransactionType(TransactionType.CREDIT)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = walletService.getTotalCredits();

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void getTotalDebits_Success() {
        // Arrange
        BigDecimal expectedTotal = BigDecimal.valueOf(1500.00);

        when(walletTransactionRepository.sumAmountByTransactionType(TransactionType.DEBIT)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = walletService.getTotalDebits();

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void getWalletsWithLowBalance_Success() {
        // Arrange
        BigDecimal threshold = BigDecimal.valueOf(100.00);
        List<Wallet> wallets = List.of(testWallet);

        when(walletRepository.findWalletsWithLowBalance(threshold)).thenReturn(wallets);
        when(walletMapper.toResponseDTOList(wallets)).thenReturn(List.of(testWalletResponseDTO));

        // Act
        List<WalletResponseDTO> result = walletService.getWalletsWithLowBalance(threshold);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testWalletResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getWalletsWithHighBalance_Success() {
        // Arrange
        BigDecimal threshold = BigDecimal.valueOf(5000.00);
        List<Wallet> wallets = List.of(testWallet);

        when(walletRepository.findWalletsWithHighBalance(threshold)).thenReturn(wallets);
        when(walletMapper.toResponseDTOList(wallets)).thenReturn(List.of(testWalletResponseDTO));

        // Act
        List<WalletResponseDTO> result = walletService.getWalletsWithHighBalance(threshold);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testWalletResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getTopWalletsByBalance_Success() {
        // Arrange
        List<Wallet> wallets = List.of(testWallet);

        when(walletRepository.findTopWalletsByBalance()).thenReturn(wallets);
        when(walletMapper.toResponseDTOList(wallets)).thenReturn(List.of(testWalletResponseDTO));

        // Act
        List<WalletResponseDTO> result = walletService.getTopWalletsByBalance();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testWalletResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getDriverWallets_Success() {
        // Arrange
        List<Wallet> wallets = List.of(testWallet);

        when(walletRepository.findDriverWallets()).thenReturn(wallets);
        when(walletMapper.toResponseDTOList(wallets)).thenReturn(List.of(testWalletResponseDTO));

        // Act
        List<WalletResponseDTO> result = walletService.getDriverWallets();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testWalletResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getPartnerWallets_Success() {
        // Arrange
        List<Wallet> wallets = List.of(testWallet);

        when(walletRepository.findPartnerWallets()).thenReturn(wallets);
        when(walletMapper.toResponseDTOList(wallets)).thenReturn(List.of(testWalletResponseDTO));

        // Act
        List<WalletResponseDTO> result = walletService.getPartnerWallets();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testWalletResponseDTO.getId(), result.get(0).getId());
    }
}
