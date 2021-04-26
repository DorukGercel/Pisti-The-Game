package com.group7.server.dto.authentication;

import com.group7.server.definitions.StatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/** DTO used for login responses*/
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginResponse extends AuthResponse{
    /** JWT token generated uniquely to the player, must be saved as bearer token*/
    @NotEmpty
    private String token;

    /** Session id of the active player in the db table*/
    @NotEmpty
    private Long sessionId;

    /** All args constructor*/
    public LoginResponse(StatusCode statusCode, String errorMessage, String token, Long sessionId){
        super(statusCode, errorMessage);
        this.token = token;
        this.sessionId = sessionId;
    }
}
