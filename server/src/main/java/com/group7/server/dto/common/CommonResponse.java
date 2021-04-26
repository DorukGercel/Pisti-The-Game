package com.group7.server.dto.common;

import com.group7.server.definitions.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;

/** DTO used as a super class for all responses*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    /** Indicates the success of the requested operation*/
    @NotEmpty
    private StatusCode statusCode;

    /** Indicates the error that occurred, null if operation was successful.*/
    @Nullable
    private String errorMessage;
}
