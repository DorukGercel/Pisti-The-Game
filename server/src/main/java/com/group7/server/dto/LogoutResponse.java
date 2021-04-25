package com.group7.server.dto;

import com.group7.server.definitions.StatusCode;
import lombok.Data;

@Data
/**DTO used for logout responses*/
public class LogoutResponse extends AuthResponse{

    /**All args constructor*/
    public LogoutResponse(StatusCode statusCode, String errorMessage){
        super(statusCode, errorMessage);
    }
}
