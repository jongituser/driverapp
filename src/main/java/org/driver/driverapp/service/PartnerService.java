package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;

import org.driver.driverapp.dto.partner.response.PartnerResponseDTO;
import org.driver.driverapp.dto.partner.request.CreatePartnerRequestDTO;
import org.driver.driverapp.dto.partner.request.UpdatePartnerRequestDTO;
import org.driver.driverapp.mapper.PartnerMapper;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.repository.PartnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;

    // 🔽 Get all partners
    public List<PartnerResponseDTO> getAllPartners() {
        return partnerRepository.findAll().stream()
                .map(partnerMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 🔍 Get partner by ID
    public Optional<PartnerResponseDTO> getPartnerById(Long id) {
        return partnerRepository.findById(id)
                .map(partnerMapper::toDTO);
    }

    // ➕ Create new partner
    public PartnerResponseDTO createPartner(CreatePartnerRequestDTO dto) {
        Partner partner = new Partner();
        partner.setName(dto.getName());
        partner.setAddress(dto.getAddress());
        partner.setPhone(dto.getPhone());
        partner.setEmail(dto.getEmail());
        return partnerMapper.toDTO(partnerRepository.save(partner));
    }

    // ✏️ Update existing partner
    public PartnerResponseDTO updatePartner(Long id, UpdatePartnerRequestDTO updatedPartner) {
        return partnerRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedPartner.getName());
                    existing.setAddress(updatedPartner.getAddress());
                    existing.setPhone(updatedPartner.getPhone());
                    existing.setEmail(updatedPartner.getEmail());
                    return partnerMapper.toDTO(partnerRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + id));
    }

    // ❌ Delete partner
    public void deletePartner(Long id) {
        partnerRepository.deleteById(id);
    }
}
