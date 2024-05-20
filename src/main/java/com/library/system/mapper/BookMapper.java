package com.library.system.mapper;

import com.library.system.dto.request.BookRequestDTO;
import com.library.system.dto.response.BookResponseDTO;
import com.library.system.entity.Book;
import org.springframework.stereotype.Component;

/**
 * Created by mahbub.islam on 18/5/2024.
 */
@Component
public class BookMapper {

    public Book toEntity(BookRequestDTO request) {
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setQuantity(request.getQuantity());
        return book;
    }

    public BookResponseDTO toDto(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setQuantity(book.getQuantity());
        return dto;
    }
}
