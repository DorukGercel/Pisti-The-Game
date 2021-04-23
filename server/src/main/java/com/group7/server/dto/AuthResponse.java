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
public class AuthResponse {
    @NotEmpty
    private StatusCode statusCode;

    @Nullable
    private String errorMessage;
}
