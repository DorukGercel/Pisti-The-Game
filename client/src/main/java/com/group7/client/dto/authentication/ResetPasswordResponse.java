package com.group7.client.dto.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.client.definitions.common.StatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/** DTO used as a reset password related responses*/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ResetPasswordResponse extends AuthResponse {
    /** Newly generated password*/
    @NotEmpty
    private String newPassword;

    /** All args constructor*/
    @JsonCreator
    public ResetPasswordResponse(@JsonProperty("statusCode") StatusCode statusCode,
                                 @JsonProperty("errorMessage") String errorMessage,
                                 @JsonProperty("password") String newPassword){
        super(statusCode, errorMessage);
        this.newPassword = newPassword;
    }
}
