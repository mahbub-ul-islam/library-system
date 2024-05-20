package com.library.system.service;

import com.library.system.dto.request.BorrowerRequestDTO;
import com.library.system.dto.response.BorrowerResponseDTO;
import com.library.system.entity.Borrower;
import com.library.system.mapper.BorrowerMapper;
import com.library.system.repository.BorrowerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

/**
 * Created by mahbub.islam on 19/5/2024.
 */
@ExtendWith(MockitoExtension.class)
class BorrowerServiceTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private BorrowerMapper borrowerMapper;

    @InjectMocks
    private BorrowerService borrowerService;

    @Test
    void test_save_Success() {
        BorrowerRequestDTO request = getAppUserDTO();
        Borrower borrower = new Borrower();
        borrower.setUsername(request.getUsername());
        borrower.setEmail(request.getEmail());
        when(borrowerMapper.toEntity(request)).thenReturn(borrower);
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(borrower);
        BorrowerResponseDTO borrowerResponseDTO = borrowerService.save(request);
        verify(borrowerRepository, times(1)).save(borrower);
    }

    @Test
    void test_findByEmail_Success() {
        String email = "myemail@gmail.com";
        Borrower borrower = new Borrower();
        borrower.setUsername("testUser");
        borrower.setEmail(email);
        when(borrowerRepository.findByEmail(email)).thenReturn(Optional.of(borrower));
        Optional<Borrower> foundUser = borrowerService.findByEmail(email);
        assertEquals(Optional.of(borrower), foundUser);
    }

    @Test
    void test_findByEmail_UserNotFound() {
        String email = "myemail@gmail.com";
        when(borrowerRepository.findByEmail(email)).thenReturn(Optional.empty());
        Optional<Borrower> foundUser = borrowerService.findByEmail(email);
        assertEquals(Optional.empty(), foundUser);
    }

    @Test
    void findAll() {
        Borrower borrower = new Borrower();
        borrower.setId("1");
        borrower.setUsername("John Doe");
        List<Borrower> borrowers = Arrays.asList(borrower);
        BorrowerResponseDTO dto1 = new BorrowerResponseDTO();
        dto1.setId("1");
        dto1.setUsername("John Doe");

        when(borrowerRepository.findAll()).thenReturn(borrowers);
        when(borrowerMapper.toDTO(borrower)).thenReturn(dto1);

        List<BorrowerResponseDTO> result = borrowerService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("John Doe", result.get(0).getUsername());

        verify(borrowerRepository, times(1)).findAll();
        verify(borrowerMapper, times(1)).toDTO(borrower);
    }

    private static BorrowerRequestDTO getAppUserDTO() {
        BorrowerRequestDTO request = new BorrowerRequestDTO();
        request.setUsername("testUser");
        request.setEmail("myemail@gmail.com");
        return request;
    }
}