package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.address.response.PostalCodeResponseDTO;
import org.driver.driverapp.model.PostalCode;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostalCodeMapper {

    PostalCodeResponseDTO toResponseDTO(PostalCode postalCode);

    List<PostalCodeResponseDTO> toResponseDTOList(List<PostalCode> postalCodes);
}
