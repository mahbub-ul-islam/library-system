package com.library.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.system.dto.request.BorrowerRequestDTO;
import com.library.system.dto.response.BorrowerResponseDTO;
import com.library.system.entity.Borrower;
import com.library.system.mapper.BorrowerMapper;
import com.library.system.service.BorrowerService;
import com.library.system.validators.BorrowerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by mahbub.islam on 19/5/2024.
 */
@ExtendWith(MockitoExtension.class)
class BorrowerControllerTest {

    @Mock
    BorrowerMapper borrowerMapper;

    @Mock
    private BorrowerService borrowerService;

    @Mock
    private BorrowerValidator borrowerValidator;

    @InjectMocks
    private BorrowerController borrowerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(borrowerController)
                .setValidator(borrowerValidator)
                .build();
    }

    @Test
    public void test_Save_Success() throws Exception {
        when(borrowerValidator.supports(BorrowerRequestDTO.class)).thenReturn(true);
        BindingResult bindingResult = mock(BindingResult.class);
        lenient().when(bindingResult.hasErrors()).thenReturn(false);
        when(borrowerService.save(any(BorrowerRequestDTO.class))).thenReturn(new BorrowerResponseDTO());
        lenient().when(borrowerMapper.toDTO(any(Borrower.class))).thenReturn(new BorrowerResponseDTO());
        mockMvc.perform(post("/borrower/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new BorrowerRequestDTO())))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andReturn();
        verify(borrowerService, times(1)).save(any(BorrowerRequestDTO.class));
    }

    @Test
    void findAllBorrowers() throws Exception {
        BorrowerResponseDTO borrower1 = new BorrowerResponseDTO();
        borrower1.setId("1");
        borrower1.setUsername("John Doe");
        BorrowerResponseDTO borrower2 = new BorrowerResponseDTO();
        borrower2.setId("2");
        borrower2.setUsername("Jane Smith");
        List<BorrowerResponseDTO> borrowers = Arrays.asList(borrower1, borrower2);

        when(borrowerService.findAll()).thenReturn(borrowers);
        mockMvc.perform(get("/borrower/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
        verify(borrowerService, times(1)).findAll();
    }
}