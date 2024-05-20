package com.library.system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by mahbub.islam on 19/5/2024.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {

    @NotNull
    @NotBlank
    private String isbn;

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String author;

    @Min(value = 1)
    @NotNull
    private Integer quantity;
}
