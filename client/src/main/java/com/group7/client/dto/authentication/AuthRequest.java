package com.group7.client.dto.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.client.dto.common.CommonRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/** DTO used for register and login requests*/
@EqualsAndHashCode(callSuper = true)
@Data
public class AuthRequest extends CommonRequest {
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