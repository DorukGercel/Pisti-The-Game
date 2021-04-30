package com.group7.server.dto.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.definitions.common.StatusCode;
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
    @JsonCreator
    public LoginResponse(@JsonProperty("statusCode") StatusCode statusCode,
                         @JsonProperty("errorMessage") String errorMessage,
                         @JsonProperty("token") String token,
                         @JsonProperty("sessionId") Long sessionId){
        super(statusCode, errorMessage);
        this.token = token;
        this.sessionId = sessionId;
    }
}
