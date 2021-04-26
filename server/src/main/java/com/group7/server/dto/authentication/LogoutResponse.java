package com.group7.server.dto.authentication;

import com.group7.server.definitions.StatusCode;
import lombok.Data;

/** DTO used for logout responses*/
@Data
public class LogoutResponse extends AuthResponse{

    /** All args constructor*/
    public LogoutResponse(StatusCode statusCode, String errorMessage){
        super(statusCode, errorMessage);
    }
}
