package com.group7.server.dto.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.dto.common.CommonRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** DTO used for register and login requests*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ResetPasswordRequest extends CommonRequest {
    /** Email of the player to register/login*/
    private String email;

    @JsonCreator
    public ResetPasswordRequest(@JsonProperty("email") String email) {
        this.email = email;
    }
}