package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.address.response.PostalCodeResponseDTO;
import org.driver.driverapp.enums.EthiopianRegion;
import org.driver.driverapp.service.PostalCodeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/postal-codes")
@RequiredArgsConstructor
public class PostalCodeController {

    private final PostalCodeService postalCodeService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<PostalCodeResponseDTO> getPostalCodeById(@PathVariable Long id) {
        PostalCodeResponseDTO response = postalCodeService.getPostalCodeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<PostalCodeResponseDTO> getPostalCodeByCode(@PathVariable String code) {
        PostalCodeResponseDTO response = postalCodeService.getPostalCodeByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/region/{region}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PostalCodeResponseDTO>> getPostalCodesByRegion(@PathVariable EthiopianRegion region) {
        List<PostalCodeResponseDTO> response = postalCodeService.getPostalCodesByRegion(region);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/region/{region}/page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<PostalCodeResponseDTO>> getPostalCodesByRegion(
            @PathVariable EthiopianRegion region,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PostalCodeResponseDTO> response = postalCodeService.getPostalCodesByRegion(region, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<PostalCodeResponseDTO>> getAllPostalCodes(@PageableDefault(size = 20) Pageable pageable) {
        Page<PostalCodeResponseDTO> response = postalCodeService.getAllPostalCodes(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PostalCodeResponseDTO>> searchPostalCodesByPattern(@RequestParam String pattern) {
        List<PostalCodeResponseDTO> response = postalCodeService.searchPostalCodesByPattern(pattern);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/region")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PostalCodeResponseDTO>> searchPostalCodesByRegionAndPattern(
            @RequestParam EthiopianRegion region,
            @RequestParam String pattern) {
        List<PostalCodeResponseDTO> response = postalCodeService.searchPostalCodesByRegionAndPattern(region, pattern);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Boolean> isPostalCodeValid(@PathVariable String code) {
        boolean isValid = postalCodeService.isPostalCodeValid(code);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/validate/{code}/region/{region}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Boolean> isPostalCodeValidInRegion(@PathVariable String code, @PathVariable EthiopianRegion region) {
        boolean isValid = postalCodeService.isPostalCodeValidInRegion(code, region);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/count/region/{region}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Long> getPostalCodeCountByRegion(@PathVariable EthiopianRegion region) {
        long count = postalCodeService.getPostalCodeCountByRegion(region);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PostalCodeResponseDTO>> getAllActivePostalCodes() {
        List<PostalCodeResponseDTO> response = postalCodeService.getAllActivePostalCodes();
        return ResponseEntity.ok(response);
    }
}
