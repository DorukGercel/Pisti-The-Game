package com.group7.server.dto;

import com.group7.server.definitions.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**DTO used as a super class for auth related responses*/
public class AuthResponse {
    /**Indicates the success of the requested operation*/
    @NotEmpty
    private StatusCode statusCode;

    /**Indicates the error that occurred, null if operation was successful.*/
    @Nullable
    private String errorMessage;
}
