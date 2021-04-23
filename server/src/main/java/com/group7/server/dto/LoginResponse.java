package com.group7.server.dto;

import com.group7.server.definitions.StatusCode;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginResponse extends AuthResponse{
    @NotEmpty
    private String token;

    @NotEmpty
    private Long sessionId;

    public LoginResponse(StatusCode statusCode, String errorMessage, String token, Long sessionId){
        super(statusCode, errorMessage);
        this.token = token;
        this.sessionId = sessionId;
    }
}
