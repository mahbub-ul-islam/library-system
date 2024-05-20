package com.library.system.service;

import com.library.system.dto.request.BookRequestDTO;
import com.library.system.dto.response.BookResponseDTO;
import com.library.system.entity.Book;
import com.library.system.entity.Borrower;
import com.library.system.exception.ResourceNotFoundException;
import com.library.system.mapper.BookMapper;
import com.library.system.repository.BookRepository;
import com.library.system.repository.BorrowerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by mahbub.islam on 19/5/2024.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BookService bookService;


    @Test
    void test_save_NewBookSuccess() {
        BookRequestDTO request = getBookDTO();
        Book book = getBook(request);
        when(bookMapper.toEntity(request)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        BookResponseDTO savedBook = bookService.save(request);
        assertEquals(bookMapper.toDto(book), savedBook);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void test_save_ExistingBookSuccess() {
        BookRequestDTO request = getBookDTO();
        Book existingBook = getBook(request);
        existingBook.setQuantity(10); // Assume initial quantity is 10
        Book updatedBook = getBook(request);
        updatedBook.setQuantity(15); // New quantity after update
        BookResponseDTO response = getBookResponseDTO(updatedBook);

        when(bookRepository.findByIsbn(request.getIsbn())).thenReturn(Collections.singletonList(existingBook));
        when(bookMapper.toDto(existingBook)).thenReturn(response);
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        BookResponseDTO savedBook = bookService.save(request);

        assertEquals(response, savedBook);
        verify(bookRepository, times(1)).save(existingBook);
        verify(bookMapper, times(1)).toDto(existingBook);
    }

    @Test
    void test_findById_ExistingBookFound() {
        String bookId = "140c7291-5afc-49c1-a253-cb156737aa81";
        Book book = new Book();
        book.setId(bookId);
        book.setIsbn("1234567890");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setQuantity(1);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        BookResponseDTO foundBook = bookService.findById(bookId);
        assertEquals(bookMapper.toDto(book), foundBook);
    }

    @Test
    void test_findById_BookNotFound() {
        String bookId = "140c7291-5afc-49c1-a253-cb156737aa81";

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.findById(bookId);
        });

        assertEquals("book not found with id: " + bookId, exception.getMessage());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapper, times(0)).toDto(any());
    }

    @Test
    void test_findByIsbn_ExistingBooksFound() {
        String isbn = "1234567890";
        List<Book> books = new ArrayList<>();
        Book book = new Book();
        book.setId("140c7291-5afc-49c1-a253-cb156737aa81");
        book.setIsbn(isbn);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setQuantity(1);
        books.add(book);
        when(bookRepository.findByIsbn(isbn)).thenReturn(books);
        List<Book> foundBooks = bookService.findByIsbn(isbn);
        assertEquals(books, foundBooks);
    }

    @Test
    public void test_testBorrowBook_Success() {
        Book book = new Book();
        book.setId("bookId");
        book.setQuantity(1);

        Borrower borrower = new Borrower();
        borrower.setId("borrowerId");
        borrower.setBorrowedQuantity(0); // Ensure it is initialized
        borrower.setBorrowedBooks(new HashSet<>()); // Initialize as a Set

        when(bookRepository.findById("bookId")).thenReturn(Optional.of(book));
        when(borrowerRepository.findById("borrowerId")).thenReturn(Optional.of(borrower));

        bookService.borrowBook("bookId", "borrowerId");

        assertEquals(0, book.getQuantity());
        assertTrue(borrower.getBorrowedBooks().contains(book));
        verify(bookRepository, times(1)).save(book);
        verify(borrowerRepository, times(1)).save(borrower);
    }


    @Test
    public void test_testBorrowBook_BookNotFound() {
        String bookId = "bookId";
        String borrowerId = "borrowerId";

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.borrowBook(bookId, borrowerId);
        });

        assertEquals("Book with id " + bookId + " not found.", exception.getMessage());
        verify(bookRepository, times(1)).findById(bookId);
        verify(borrowerRepository, times(0)).findById(anyString());
    }


    @Test
    public void test_testBorrowBook_BorrowerNotFound() {
        String bookId = "bookId";
        String borrowerId = "borrowerId";

        Book book = new Book();
        book.setId(bookId);
        book.setQuantity(1);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.borrowBook(bookId, borrowerId);
        });

        assertEquals("Borrower with id " + borrowerId + " not found.", exception.getMessage());
        verify(bookRepository, times(1)).findById(bookId);
        verify(borrowerRepository, times(1)).findById(borrowerId);
    }


    @Test
    public void test_testBorrowBook_BookNotAvailable() {
        Book book = new Book();
        book.setId("bookId");
        book.setQuantity(0);
        Borrower borrower = new Borrower();
        borrower.setId("borrowerId");
        when(bookRepository.findById("bookId")).thenReturn(Optional.of(book));
        when(borrowerRepository.findById("borrowerId")).thenReturn(Optional.of(borrower));
        assertThrows(RuntimeException.class, () -> bookService.borrowBook("bookId", "borrowerId"));
    }

    @Test
    public void testReturnBook_Success() {
        Book book = new Book();
        book.setId("bookId");
        book.setQuantity(1);

        Borrower borrower = new Borrower();
        borrower.setId("borrowerId");
        borrower.setBorrowedQuantity(1); // Ensure it is initialized
        borrower.setBorrowedBooks(new HashSet<>()); // Initialize as a Set
        borrower.getBorrowedBooks().add(book);

        when(bookRepository.findById("bookId")).thenReturn(Optional.of(book));
        when(borrowerRepository.findById("borrowerId")).thenReturn(Optional.of(borrower));

        bookService.returnBook("bookId", "borrowerId");

        assertEquals(2, book.getQuantity());
        assertFalse(borrower.getBorrowedBooks().contains(book));
        verify(bookRepository, times(1)).save(book);
        verify(borrowerRepository, times(1)).save(borrower);
    }

    @Test
    public void testReturnBook_BookNotFound() {
        String bookId = "bookId";
        String borrowerId = "borrowerId";

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.returnBook(bookId, borrowerId);
        });

        assertEquals("Book with id " + bookId + " not found.", exception.getMessage());
        verify(bookRepository, times(1)).findById(bookId);
        verify(borrowerRepository, times(0)).findById(anyString());
    }

    @Test
    public void testReturnBook_BorrowerNotFound() {
        String bookId = "bookId";
        String borrowerId = "borrowerId";

        Book book = new Book();
        book.setId(bookId);
        book.setQuantity(1);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.returnBook(bookId, borrowerId);
        });

        assertEquals("Borrower with id " + borrowerId + " not found.", exception.getMessage());
        verify(bookRepository, times(1)).findById(bookId);
        verify(borrowerRepository, times(1)).findById(borrowerId);
    }

    @Test
    void findAll_WithBooks_ReturnsPagedBooks() {
        int page = 0;
        int size = 5;

        List<Book> books = IntStream.range(0, size)
                .mapToObj(i -> {
                    Book book = new Book();
                    book.setId("bookId" + i);
                    book.setTitle("Title" + i);
                    book.setAuthor("Author" + i);
                    book.setIsbn("ISBN" + i);
                    book.setQuantity(1);
                    return book;
                }).collect(Collectors.toList());

        List<BookResponseDTO> bookDTOs = books.stream()
                .map(book -> {
                    BookResponseDTO dto = new BookResponseDTO();
                    dto.setId(book.getId());
                    dto.setTitle(book.getTitle());
                    dto.setAuthor(book.getAuthor());
                    dto.setIsbn(book.getIsbn());
                    dto.setQuantity(book.getQuantity());
                    return dto;
                }).collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        for (int i = 0; i < books.size(); i++) {
            when(bookMapper.toDto(books.get(i))).thenReturn(bookDTOs.get(i));
        }

        Page<BookResponseDTO> result = bookService.findAll(page, size);

        assertEquals(size, result.getSize());
        assertEquals(books.size(), result.getTotalElements());
        assertEquals(bookDTOs, result.getContent());
        verify(bookRepository, times(1)).findAll(pageable);
        for (Book book : books) {
            verify(bookMapper, times(1)).toDto(book);
        }
    }

    private static Book getBook(BookRequestDTO request) {
        Book book = new Book();
        book.setId("1");
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setQuantity(request.getQuantity());
        return book;
    }

    private static BookRequestDTO getBookDTO() {
        BookRequestDTO request = new BookRequestDTO();
        request.setIsbn("1234567890");
        request.setTitle("Test Book");
        request.setAuthor("Test Author");
        request.setQuantity(1);
        return request;
    }

    private BookResponseDTO getBookResponseDTO(Book book) {
        return new BookResponseDTO(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(), book.getQuantity());
    }
}