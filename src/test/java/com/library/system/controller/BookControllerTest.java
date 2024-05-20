package com.library.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.system.dto.request.BookRequestDTO;
import com.library.system.dto.response.BookResponseDTO;
import com.library.system.entity.Book;
import com.library.system.entity.Borrower;
import com.library.system.helper.BookHelper;
import com.library.system.mapper.BookMapper;
import com.library.system.service.BookService;
import com.library.system.validators.BookValidator;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by mahbub.islam on 19/5/2024.
 */
@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookHelper bookHelper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookService bookService;

    @Mock
    private BookValidator validator;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookController)
                .setValidator(validator)
                .build();
    }

    @Test
    void test_findById() throws Exception {
        String bookId = "140c7291-5afc-49c1-a253-cb156737aa81";
        Book book = new Book();
        book.setId(bookId);
        lenient().when(bookService.findById(bookId)).thenReturn(new BookResponseDTO());
        BookResponseDTO bookDTO = getBookDTO(bookId);
        lenient().when(bookMapper.toDto(book)).thenReturn(bookDTO);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
        mockMvc.perform(get("/book/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_save_Success() throws Exception {
        when(validator.supports(BookRequestDTO.class)).thenReturn(true);
        BindingResult bindingResult = mock(BindingResult.class);
        lenient().when(bindingResult.hasErrors()).thenReturn(false);
        when(bookService.save(any(BookRequestDTO.class))).thenReturn(new BookResponseDTO());
        lenient().when(bookMapper.toDto(any(Book.class))).thenReturn(new BookResponseDTO());
        mockMvc.perform(post("/book/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new BookRequestDTO())))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andReturn();
        verify(bookService, times(1)).save(any(BookRequestDTO.class));
    }

    @Test
    void test_borrowBook_ValidInput() throws Exception {
        String bookId = "bookId";
        String borrowerId = "borrowerId";
        Borrower borrower = new Borrower();
        borrower.setUsername("username");
        doNothing().when(bookService).borrowBook(bookId, borrowerId);
        mockMvc.perform(post("/book/borrow/{bookId}/{borrowerId}", bookId, borrowerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data")
                        .value(
                                "Book borrowed successfully with bookId: "
                                        + bookId
                        )
                );
    }

    @Test
    void test_returnBook_ValidInput() throws Exception {
        String bookId = "bookId";
        String borrowerId = "borrowerId";
        Borrower mockAppUser = new Borrower();
        mockAppUser.setUsername("username");
        doNothing().when(bookService).returnBook(bookId, borrowerId);
        mockMvc.perform(post("/book/return/{bookId}/{borrowerId}", bookId, borrowerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data")
                        .value(
                                "Book returned successfully with bookId: "
                                        + bookId
                        )
                );
    }

    @Test
    void test_findAll() throws Exception {
        mockMvc.perform(get("/book/books")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk());
    }

    private static BookResponseDTO getBookDTO(String bookId) {
        BookResponseDTO bookDTO = new BookResponseDTO();
        bookDTO.setId(bookId);
        bookDTO.setIsbn("ISBN-123");
        bookDTO.setTitle("Test Book");
        bookDTO.setAuthor("Test Author");
        bookDTO.setQuantity(5);
        return bookDTO;
    }
}