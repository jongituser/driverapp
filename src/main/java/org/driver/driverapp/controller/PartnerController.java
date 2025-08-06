package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.repository.PartnerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerRepository partnerRepository;

    @GetMapping
    public List<Partner> getAll() {
        return partnerRepository.findAll();
    }

    @PostMapping
    public Partner create(@RequestBody Partner partner) {
        return partnerRepository.save(partner);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partner> getOne(@PathVariable Long id) {
        return partnerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
