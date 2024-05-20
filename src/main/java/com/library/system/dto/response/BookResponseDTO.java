package com.library.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by mahbub.islam on 19/5/2024.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {

    private String id;
    private String isbn;
    private String title;
    private String author;
    private Integer quantity;
}
