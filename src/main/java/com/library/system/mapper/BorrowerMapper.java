package com.library.system.mapper;

import com.library.system.dto.request.BorrowerRequestDTO;
import com.library.system.dto.response.BorrowerResponseDTO;
import com.library.system.entity.Borrower;
import org.springframework.stereotype.Component;

/**
 * Created by mahbub.islam on 18/5/2024.
 */
@Component
public class BorrowerMapper {

    public Borrower toEntity(BorrowerRequestDTO dto) {
        Borrower entity = new Borrower();
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        return entity;
    }

    public BorrowerResponseDTO toDTO(Borrower entity) {
        BorrowerResponseDTO dto = new BorrowerResponseDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setBorrowedQuantity(entity.getBorrowedQuantity());
        dto.setIsActiveBorrowed(entity.getIsActiveBorrowed());
        return dto;
    }
}
