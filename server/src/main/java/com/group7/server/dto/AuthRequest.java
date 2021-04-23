package com.group7.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class AuthRequest {

    //TODO: Use either user name or password

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String email;

}