package com.group7.server.dto.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.dto.common.CommonRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/** DTO used for logout requests*/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class LogoutRequest extends CommonRequest {

    /** Session id of the active player who requests logout*/
    @NotEmpty
    private Long sessionId;

    /** All args constructor*/
    @JsonCreator
    public LogoutRequest(@JsonProperty("sessionId") Long sessionId) {
        this.sessionId = sessionId;
    }
}
