package com.library.system.validators;

import com.library.system.dto.request.BookRequestDTO;
import com.library.system.entity.Book;
import com.library.system.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * Created by mahbub.islam on 19/5/2024.
 */
@Component
@RequiredArgsConstructor
public class BookValidator implements Validator {

    private final BookService bookService;

    @Override
    public boolean supports(Class<?> clazz) {
        return BookRequestDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BookRequestDTO dto = (BookRequestDTO) target;
        List<Book> existingBooks = bookService.findByIsbn(dto.getIsbn());
        for (Book existingBook : existingBooks) {
            if (!existingBook.getTitle().equals(dto.getTitle()) ||
                    !existingBook.getAuthor().equals(dto.getAuthor())) {
                errors.rejectValue("isbn", "",
                        "Book with this ISBN already exists with different title or author");
                break;
            }
        }
    }
}

