package com.group7.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**DTO used for logout requests*/
public class LogoutRequest {

    /**Session id of the active player who requests logout*/
    @NotEmpty
    private Long sessionId;
}
