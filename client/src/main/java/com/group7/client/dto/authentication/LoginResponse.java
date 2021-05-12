package com.group7.client.dto.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.client.definitions.common.StatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/** DTO used for login responses*/
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginResponse extends AuthResponse{
    /** JWT token generated uniquely to the player, must be saved as bearer token*/
    @NotNull
    private String token;

    /** Session id of the active player in the db table*/
    @NotNull
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
