package com.group7.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/** DTO used for register and login requests*/
@Data
@AllArgsConstructor
public class AuthRequest {
    //TODO: Use either user name or password
    /** Username of the player to register/login*/
    @NotEmpty
    private String username;

    /** Password of the player to register/login*/
    @NotEmpty
    private String password;

    /** Email of the player to register/login*/
    @NotEmpty
    private String email;

}