package com.cafe.api.dto.request;

import lombok.Data;

@Data
public class UserRequestDTO {
    private Integer id;
    private String name;
    private String contactNumber;
    private String email;
    private String password;
    private Boolean status;
}