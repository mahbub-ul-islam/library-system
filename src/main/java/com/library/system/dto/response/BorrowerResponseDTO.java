package com.library.system.dto.response;

import lombok.Data;

/**
 * Created by mahbub.islam on 19/5/2024.
 */
@Data
public class BorrowerResponseDTO {

    private String id;
    private String username;
    private String email;
    private Integer borrowedQuantity;
    private Boolean isActiveBorrowed;
}
