package org.driver.driverapp.service;

import org.driver.driverapp.dto.address.response.PostalCodeResponseDTO;
import org.driver.driverapp.enums.EthiopianRegion;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.PostalCodeMapper;
import org.driver.driverapp.model.PostalCode;
import org.driver.driverapp.repository.PostalCodeRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostalCodeServiceTest {

    @Mock
    private PostalCodeRepository postalCodeRepository;

    @Mock
    private PostalCodeMapper postalCodeMapper;

    @InjectMocks
    private PostalCodeService postalCodeService;

    private PostalCode testPostalCode;
    private PostalCodeResponseDTO testPostalCodeResponseDTO;

    @BeforeEach
    void setUp() {
        testPostalCode = PostalCode.builder()
                .id(1L)
                .region(EthiopianRegion.ADDIS_ABABA)
                .code("1000")
                .description("Addis Ababa Central")
                .active(true)
                .build();

        testPostalCodeResponseDTO = PostalCodeResponseDTO.builder()
                .id(1L)
                .region(EthiopianRegion.ADDIS_ABABA)
                .code("1000")
                .description("Addis Ababa Central")
                .active(true)
                .build();
    }

    @Test
    void getPostalCodeById_Success() {
        // Arrange
        Long postalCodeId = 1L;
        when(postalCodeRepository.findById(postalCodeId)).thenReturn(Optional.of(testPostalCode));
        when(postalCodeMapper.toResponseDTO(testPostalCode)).thenReturn(testPostalCodeResponseDTO);

        // Act
        PostalCodeResponseDTO result = postalCodeService.getPostalCodeById(postalCodeId);

        // Assert
        assertNotNull(result);
        assertEquals(testPostalCodeResponseDTO.getId(), result.getId());
        assertEquals(testPostalCodeResponseDTO.getCode(), result.getCode());
        assertEquals(testPostalCodeResponseDTO.getRegion(), result.getRegion());
    }

    @Test
    void getPostalCodeById_NotFound_ThrowsException() {
        // Arrange
        Long postalCodeId = 999L;
        when(postalCodeRepository.findById(postalCodeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                postalCodeService.getPostalCodeById(postalCodeId));
    }

    @Test
    void getPostalCodeByCode_Success() {
        // Arrange
        String code = "1000";
        when(postalCodeRepository.findByCodeAndActiveTrue(code)).thenReturn(Optional.of(testPostalCode));
        when(postalCodeMapper.toResponseDTO(testPostalCode)).thenReturn(testPostalCodeResponseDTO);

        // Act
        PostalCodeResponseDTO result = postalCodeService.getPostalCodeByCode(code);

        // Assert
        assertNotNull(result);
        assertEquals(testPostalCodeResponseDTO.getId(), result.getId());
        assertEquals(testPostalCodeResponseDTO.getCode(), result.getCode());
    }

    @Test
    void getPostalCodeByCode_NotFound_ThrowsException() {
        // Arrange
        String code = "9999";
        when(postalCodeRepository.findByCodeAndActiveTrue(code)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                postalCodeService.getPostalCodeByCode(code));
    }

    @Test
    void getPostalCodesByRegion_Success() {
        // Arrange
        EthiopianRegion region = EthiopianRegion.ADDIS_ABABA;
        List<PostalCode> postalCodes = List.of(testPostalCode);
        List<PostalCodeResponseDTO> postalCodeResponseDTOs = List.of(testPostalCodeResponseDTO);

        when(postalCodeRepository.findByRegionAndActiveTrue(region)).thenReturn(postalCodes);
        when(postalCodeMapper.toResponseDTOList(postalCodes)).thenReturn(postalCodeResponseDTOs);

        // Act
        List<PostalCodeResponseDTO> result = postalCodeService.getPostalCodesByRegion(region);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPostalCodeResponseDTO.getId(), result.get(0).getId());
        assertEquals(testPostalCodeResponseDTO.getRegion(), result.get(0).getRegion());
    }

    @Test
    void getPostalCodesByRegion_WithPagination_Success() {
        // Arrange
        EthiopianRegion region = EthiopianRegion.ADDIS_ABABA;
        Pageable pageable = PageRequest.of(0, 20);
        Page<PostalCode> postalCodePage = new PageImpl<>(List.of(testPostalCode));

        when(postalCodeRepository.findByRegionAndActiveTrue(region, pageable)).thenReturn(postalCodePage);
        when(postalCodeMapper.toResponseDTO(testPostalCode)).thenReturn(testPostalCodeResponseDTO);

        // Act
        Page<PostalCodeResponseDTO> result = postalCodeService.getPostalCodesByRegion(region, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testPostalCodeResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getAllPostalCodes_WithPagination_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<PostalCode> postalCodePage = new PageImpl<>(List.of(testPostalCode));

        when(postalCodeRepository.findByActiveTrue(pageable)).thenReturn(postalCodePage);
        when(postalCodeMapper.toResponseDTO(testPostalCode)).thenReturn(testPostalCodeResponseDTO);

        // Act
        Page<PostalCodeResponseDTO> result = postalCodeService.getAllPostalCodes(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testPostalCodeResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void searchPostalCodesByPattern_Success() {
        // Arrange
        String pattern = "100";
        List<PostalCode> postalCodes = List.of(testPostalCode);
        List<PostalCodeResponseDTO> postalCodeResponseDTOs = List.of(testPostalCodeResponseDTO);

        when(postalCodeRepository.findByCodePattern(pattern)).thenReturn(postalCodes);
        when(postalCodeMapper.toResponseDTOList(postalCodes)).thenReturn(postalCodeResponseDTOs);

        // Act
        List<PostalCodeResponseDTO> result = postalCodeService.searchPostalCodesByPattern(pattern);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPostalCodeResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void searchPostalCodesByRegionAndPattern_Success() {
        // Arrange
        EthiopianRegion region = EthiopianRegion.ADDIS_ABABA;
        String pattern = "100";
        List<PostalCode> postalCodes = List.of(testPostalCode);
        List<PostalCodeResponseDTO> postalCodeResponseDTOs = List.of(testPostalCodeResponseDTO);

        when(postalCodeRepository.findByRegionAndCodePattern(region, pattern)).thenReturn(postalCodes);
        when(postalCodeMapper.toResponseDTOList(postalCodes)).thenReturn(postalCodeResponseDTOs);

        // Act
        List<PostalCodeResponseDTO> result = postalCodeService.searchPostalCodesByRegionAndPattern(region, pattern);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPostalCodeResponseDTO.getId(), result.get(0).getId());
        assertEquals(testPostalCodeResponseDTO.getRegion(), result.get(0).getRegion());
    }

    @Test
    void isPostalCodeValid_ValidCode_ReturnsTrue() {
        // Arrange
        String code = "1000";
        when(postalCodeRepository.existsByCodeAndActiveTrue(code)).thenReturn(true);

        // Act
        boolean result = postalCodeService.isPostalCodeValid(code);

        // Assert
        assertTrue(result);
    }

    @Test
    void isPostalCodeValid_InvalidCode_ReturnsFalse() {
        // Arrange
        String code = "9999";
        when(postalCodeRepository.existsByCodeAndActiveTrue(code)).thenReturn(false);

        // Act
        boolean result = postalCodeService.isPostalCodeValid(code);

        // Assert
        assertFalse(result);
    }

    @Test
    void isPostalCodeValidInRegion_ValidCodeInRegion_ReturnsTrue() {
        // Arrange
        String code = "1000";
        EthiopianRegion region = EthiopianRegion.ADDIS_ABABA;
        when(postalCodeRepository.existsByRegionAndCodeAndActiveTrue(region, code)).thenReturn(true);

        // Act
        boolean result = postalCodeService.isPostalCodeValidInRegion(code, region);

        // Assert
        assertTrue(result);
    }

    @Test
    void isPostalCodeValidInRegion_InvalidCodeInRegion_ReturnsFalse() {
        // Arrange
        String code = "9999";
        EthiopianRegion region = EthiopianRegion.ADDIS_ABABA;
        when(postalCodeRepository.existsByRegionAndCodeAndActiveTrue(region, code)).thenReturn(false);

        // Act
        boolean result = postalCodeService.isPostalCodeValidInRegion(code, region);

        // Assert
        assertFalse(result);
    }

    @Test
    void getPostalCodeCountByRegion_Success() {
        // Arrange
        EthiopianRegion region = EthiopianRegion.ADDIS_ABABA;
        long expectedCount = 5L;
        when(postalCodeRepository.countByRegionAndActiveTrue(region)).thenReturn(expectedCount);

        // Act
        long result = postalCodeService.getPostalCodeCountByRegion(region);

        // Assert
        assertEquals(expectedCount, result);
    }

    @Test
    void getAllActivePostalCodes_Success() {
        // Arrange
        List<PostalCode> postalCodes = List.of(testPostalCode);
        List<PostalCodeResponseDTO> postalCodeResponseDTOs = List.of(testPostalCodeResponseDTO);

        when(postalCodeRepository.findByActiveTrue()).thenReturn(postalCodes);
        when(postalCodeMapper.toResponseDTOList(postalCodes)).thenReturn(postalCodeResponseDTOs);

        // Act
        List<PostalCodeResponseDTO> result = postalCodeService.getAllActivePostalCodes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPostalCodeResponseDTO.getId(), result.get(0).getId());
    }
}
