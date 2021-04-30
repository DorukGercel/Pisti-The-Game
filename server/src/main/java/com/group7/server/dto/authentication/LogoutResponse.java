package com.group7.server.dto.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.definitions.common.StatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** DTO used for logout responses*/
@EqualsAndHashCode(callSuper = true)
@Data
public class LogoutResponse extends AuthResponse{

    /** All args constructor*/
    @JsonCreator
    public LogoutResponse(@JsonProperty("statusCode") StatusCode statusCode,
                          @JsonProperty("errorMessage") String errorMessage){
        super(statusCode, errorMessage);
    }
}
