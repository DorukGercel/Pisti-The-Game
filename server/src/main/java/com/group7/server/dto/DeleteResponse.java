package com.group7.server.dto;

import com.group7.server.definitions.StatusCode;
import lombok.Data;

@Data
public class DeleteResponse extends AuthResponse{

    public DeleteResponse(StatusCode statusCode, String errorMessage){
        super(statusCode, errorMessage);
    }
}
