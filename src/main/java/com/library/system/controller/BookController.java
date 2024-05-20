package com.library.system.controller;

import com.library.system.dto.request.BookRequestDTO;
import com.library.system.dto.response.BookResponseDTO;
import com.library.system.helper.BookHelper;
import com.library.system.service.BookService;
import com.library.system.validators.BookValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.library.system.exception.ApiError.fieldError;
import static com.library.system.utils.ResponseBuilder.error;
import static com.library.system.utils.ResponseBuilder.success;
import static com.library.system.utils.StringUtils.toJson;
import static org.springframework.http.ResponseEntity.badRequest;

/**
 * Created by mahbub.islam on 18/5/2024.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Bool API")
@RequestMapping(path = "book")
public class BookController {

    private final BookService bookService;

    private final BookValidator validator;

    private final BookHelper helper;

    @GetMapping("/{id}")
    @Operation(summary = "get book by id")
    public ResponseEntity<JSONObject> findById(@PathVariable("id") String id) {
        BookResponseDTO dto = bookService.findById(id);
        log.info("get book by id: {}, response: {} ", id, toJson(dto));
        return new ResponseEntity<>(success(dto).getJson(), HttpStatus.OK);
    }

    @PostMapping("/save")
    @Operation(summary = "save book")
    public ResponseEntity<JSONObject> save(@Valid @RequestBody BookRequestDTO request,
                                           BindingResult bindingResult) {
        ValidationUtils.invokeValidator(validator, request, bindingResult);
        if (bindingResult.hasErrors()) {
            return badRequest().body(error(fieldError(bindingResult)).getJson());
        }
        BookResponseDTO dto = bookService.save(request);
        log.info("book save response: {} ", toJson(dto));
        return new ResponseEntity<>(success(dto).getJson(), HttpStatus.CREATED);
    }

    @PostMapping("/borrow/{bookId}/{borrowerId}")
    @Operation(summary = "borrow book by book id and borrower id")
    public ResponseEntity<JSONObject> borrowBook(@PathVariable String bookId,
                                                 @PathVariable String borrowerId) {
        bookService.borrowBook(bookId, borrowerId);
        return ResponseEntity.ok(success("Book borrowed successfully with bookId: " + bookId).getJson());
    }

    @PostMapping("/return/{bookId}/{borrowerId}")
    @Operation(summary = "return book by book id and borrower id")
    public ResponseEntity<JSONObject> returnBook(@PathVariable String bookId, @PathVariable String borrowerId) {
        bookService.returnBook(bookId, borrowerId);
        return ResponseEntity.ok(success("Book returned successfully with bookId: " + bookId).getJson());
    }

    @GetMapping("/books")
    @Operation(summary = "get all paginated books by page and size")
    public ResponseEntity<JSONObject> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Map<Object, Object> responseMap = new HashMap<>();
        Page<BookResponseDTO> dtos = bookService.findAll(page, size);
        helper.pagination(page, dtos, responseMap);
        return new ResponseEntity<>(success(responseMap).getJson(), HttpStatus.OK);
    }
}