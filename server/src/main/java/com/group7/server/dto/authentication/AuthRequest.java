package com.group7.server.dto.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/** DTO used for register and login requests*/
@Data
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

    @JsonCreator
    public AuthRequest(@JsonProperty("username") String username,
                       @JsonProperty("password") String password,
                       @JsonProperty("email") String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}