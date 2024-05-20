package com.library.system.validators;

import com.library.system.dto.request.BorrowerRequestDTO;
import com.library.system.entity.Borrower;
import com.library.system.service.BorrowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

import static com.library.system.utils.Constants.ALREADY_EXIST;

/**
 * Created by mahbub.islam on 19/5/2024.
 */
@Component
@RequiredArgsConstructor
public class BorrowerValidator implements Validator {

    private final BorrowerService borrowerService;

    @Override
    public boolean supports(Class<?> clazz) {
        return BorrowerRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BorrowerRequestDTO dto = (BorrowerRequestDTO) target;
        Optional<Borrower> user = borrowerService.findByEmail(dto.getEmail());
        if (user.isPresent()) {
            errors.rejectValue("email", "", ALREADY_EXIST);
        }
    }
}

