package com.group7.server.dto.authentication;

import com.group7.server.definitions.StatusCode;
import com.group7.server.dto.common.CommonResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** DTO used as a super class for auth related responses*/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AuthResponse extends CommonResponse {
    /** All args constructor*/
    public AuthResponse(StatusCode statusCode, String errorMessage){
        super(statusCode, errorMessage);
    }
}
