package com.library.system.controller;

import com.library.system.dto.request.BorrowerRequestDTO;
import com.library.system.dto.response.BookResponseDTO;
import com.library.system.dto.response.BorrowerResponseDTO;
import com.library.system.service.BorrowerService;
import com.library.system.validators.BorrowerValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@Tag(name = "Borrower API")
@RequestMapping(path = "/borrower")
public class BorrowerController {

    private final BorrowerValidator borrowerValidator;

    private final BorrowerService borrowerService;

    @PostMapping("/register")
    @Operation(summary = "register a new user")
    public ResponseEntity<JSONObject> save(@Valid @RequestBody BorrowerRequestDTO request, BindingResult bindingResult) {
        ValidationUtils.invokeValidator(borrowerValidator, request, bindingResult);

        if (bindingResult.hasErrors()) {
            return badRequest().body(error(fieldError(bindingResult)).getJson());
        }
        BorrowerResponseDTO dto = borrowerService.save(request);
        log.info("register user response: {} ", toJson(dto));
        return new ResponseEntity<>(success(dto).getJson(), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @Operation(summary = "get all Borrower")
    public ResponseEntity<JSONObject> findById() {
        List<BorrowerResponseDTO> dto = borrowerService.findAll();
        log.info("get all Borrower : ", toJson(dto));
        return new ResponseEntity<>(success(dto).getJson(), HttpStatus.OK);
    }
}

