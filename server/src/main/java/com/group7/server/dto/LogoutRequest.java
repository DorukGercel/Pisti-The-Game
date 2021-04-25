package com.group7.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/** DTO used for logout requests*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogoutRequest {

    /** Session id of the active player who requests logout*/
    @NotEmpty
    private Long sessionId;
}
