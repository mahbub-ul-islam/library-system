package com.library.system.service;

import com.library.system.dto.request.BookRequestDTO;
import com.library.system.dto.response.BookResponseDTO;
import com.library.system.entity.Book;
import com.library.system.entity.Borrower;
import com.library.system.exception.ResourceNotFoundException;
import com.library.system.mapper.BookMapper;
import com.library.system.repository.BookRepository;
import com.library.system.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.library.system.utils.StringUtils.toJson;
import static java.util.Objects.nonNull;

/**
 * Created by mahbub.islam on 18/5/2024.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final BorrowerRepository borrowerRepository;

    @Transactional
    public BookResponseDTO save(BookRequestDTO request) {
        return bookRepository.findByIsbn(request.getIsbn()).stream()
                .filter(book -> book.getTitle().equals(request.getTitle())
                        && book.getAuthor().equals(request.getAuthor()))
                .findFirst()
                .map(book -> {
                    book.setQuantity(book.getQuantity() + request.getQuantity());
                    return bookMapper.toDto(bookRepository.save(book));
                })
                .orElseGet(() -> {
                    Book newBook = bookMapper.toEntity(request);
                    log.info("book saved : {} ", toJson(newBook));
                    return bookMapper.toDto(bookRepository.save(newBook));
                });
    }

    @Transactional(readOnly = true)
    public BookResponseDTO findById(String id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> {
            log.error("book not found with id: {}", id);
            return new ResourceNotFoundException("book not found with id: " + id);
        });
        log.info("get book by id: {}, book: {} ", id, toJson(book));
        return bookMapper.toDto(book);
    }

    @Transactional(readOnly = true)
    public List<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Transactional
    public void borrowBook(String bookId, String borrowerId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new ResourceNotFoundException("Book with id " + bookId + " not found.")
        );
        Borrower borrower = borrowerRepository.findById(borrowerId).orElseThrow(
                () -> new ResourceNotFoundException("Borrower with id " + borrowerId + " not found.")
        );
        if (nonNull(book.getQuantity()) && book.getQuantity() > 0) {
            book.setQuantity(book.getQuantity() - 1);
            bookRepository.save(book);
            borrower.getBorrowedBooks().add(book);
            borrower.setBorrowedQuantity(nonNull(borrower.getBorrowedQuantity()) ? borrower.getBorrowedQuantity() + 1 : 0);
            borrower.setIsActiveBorrowed(true);
            borrowerRepository.save(borrower);
        } else {
            throw new ResourceNotFoundException("Book with id " + bookId +
                    " is not available for borrowing.");
        }
    }

    @Transactional
    public void returnBook(String bookId, String borrowerId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new ResourceNotFoundException("Book with id " + bookId + " not found.")
        );
        Borrower borrower = borrowerRepository.findById(borrowerId).orElseThrow(
                () -> new ResourceNotFoundException("Borrower with id " + borrowerId + " not found.")
        );
        if(nonNull(book.getQuantity())) {
            book.setQuantity(book.getQuantity() + 1);
        }
        bookRepository.save(book);
        if(nonNull(borrower.getBorrowedQuantity())) {
            borrower.setBorrowedQuantity(borrower.getBorrowedQuantity() - 1);
            borrower.setIsActiveBorrowed(borrower.getBorrowedQuantity() == 0);
        }
        borrower.getBorrowedBooks().remove(book);
        borrowerRepository.save(borrower);
    }

    public Page<BookResponseDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findAll(pageable);

        List<BookResponseDTO> bookDTOs = bookPage.getContent().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(bookDTOs, pageable, bookPage.getTotalElements());
    }
}
