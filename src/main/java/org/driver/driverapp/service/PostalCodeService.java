package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.address.response.PostalCodeResponseDTO;
import org.driver.driverapp.enums.EthiopianRegion;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.PostalCodeMapper;
import org.driver.driverapp.model.PostalCode;
import org.driver.driverapp.repository.PostalCodeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostalCodeService {

    private final PostalCodeRepository postalCodeRepository;
    private final PostalCodeMapper postalCodeMapper;

    @Transactional(readOnly = true)
    public PostalCodeResponseDTO getPostalCodeById(Long id) {
        log.info("Getting postal code by id: {}", id);
        
        PostalCode postalCode = postalCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postal code not found with id: " + id));
        
        return postalCodeMapper.toResponseDTO(postalCode);
    }

    @Transactional(readOnly = true)
    public PostalCodeResponseDTO getPostalCodeByCode(String code) {
        log.info("Getting postal code by code: {}", code);
        
        PostalCode postalCode = postalCodeRepository.findByCodeAndActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException("Postal code not found with code: " + code));
        
        return postalCodeMapper.toResponseDTO(postalCode);
    }

    @Transactional(readOnly = true)
    public List<PostalCodeResponseDTO> getPostalCodesByRegion(EthiopianRegion region) {
        log.info("Getting postal codes by region: {}", region);
        
        List<PostalCode> postalCodes = postalCodeRepository.findByRegionAndActiveTrue(region);
        return postalCodeMapper.toResponseDTOList(postalCodes);
    }

    @Transactional(readOnly = true)
    public Page<PostalCodeResponseDTO> getPostalCodesByRegion(EthiopianRegion region, Pageable pageable) {
        log.info("Getting postal codes by region: {} with pagination", region);
        
        Page<PostalCode> postalCodes = postalCodeRepository.findByRegionAndActiveTrue(region, pageable);
        return postalCodes.map(postalCodeMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<PostalCodeResponseDTO> getAllPostalCodes(Pageable pageable) {
        log.info("Getting all postal codes with pagination");
        
        Page<PostalCode> postalCodes = postalCodeRepository.findByActiveTrue(pageable);
        return postalCodes.map(postalCodeMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<PostalCodeResponseDTO> searchPostalCodesByPattern(String codePattern) {
        log.info("Searching postal codes by pattern: {}", codePattern);
        
        List<PostalCode> postalCodes = postalCodeRepository.findByCodePattern(codePattern);
        return postalCodeMapper.toResponseDTOList(postalCodes);
    }

    @Transactional(readOnly = true)
    public List<PostalCodeResponseDTO> searchPostalCodesByRegionAndPattern(EthiopianRegion region, String codePattern) {
        log.info("Searching postal codes by region: {} and pattern: {}", region, codePattern);
        
        List<PostalCode> postalCodes = postalCodeRepository.findByRegionAndCodePattern(region, codePattern);
        return postalCodeMapper.toResponseDTOList(postalCodes);
    }

    @Transactional(readOnly = true)
    public boolean isPostalCodeValid(String code) {
        return postalCodeRepository.existsByCodeAndActiveTrue(code);
    }

    @Transactional(readOnly = true)
    public boolean isPostalCodeValidInRegion(String code, EthiopianRegion region) {
        return postalCodeRepository.existsByRegionAndCodeAndActiveTrue(region, code);
    }

    @Transactional(readOnly = true)
    public long getPostalCodeCountByRegion(EthiopianRegion region) {
        return postalCodeRepository.countByRegionAndActiveTrue(region);
    }

    @Transactional(readOnly = true)
    public List<PostalCodeResponseDTO> getAllActivePostalCodes() {
        log.info("Getting all active postal codes");
        
        List<PostalCode> postalCodes = postalCodeRepository.findByActiveTrue();
        return postalCodeMapper.toResponseDTOList(postalCodes);
    }
}
