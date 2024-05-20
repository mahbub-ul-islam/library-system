package com.library.system.service;

import com.library.system.dto.request.BorrowerRequestDTO;
import com.library.system.dto.response.BorrowerResponseDTO;
import com.library.system.entity.Borrower;
import com.library.system.mapper.BorrowerMapper;
import com.library.system.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by mahbub.islam on 18/5/2024.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    private final BorrowerMapper borrowerMapper;

    @Transactional
    public BorrowerResponseDTO save(BorrowerRequestDTO request) {
        Borrower borrower = borrowerMapper.toEntity(request);
        return borrowerMapper.toDTO(borrowerRepository.save(borrower));
    }

    @Transactional(readOnly = true)
    public Optional<Borrower> findByEmail(String email) {
        return borrowerRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<BorrowerResponseDTO> findAll() {
        List<Borrower> borrowerList = borrowerRepository.findAll();
        return borrowerList.stream().map(
                borrower -> borrowerMapper.toDTO(borrower))
                .collect(Collectors.toList());
    }
}
