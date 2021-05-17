package com.group7.server.dto.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.dto.common.CommonRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/** DTO used for start new game requests*/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class InitGameRequest extends CommonRequest {
    /** Session id of the active player who requests to start new game*/
    @NotEmpty
    private Long sessionId;

    @JsonCreator
    public InitGameRequest(@JsonProperty("sessionId") Long sessionId) {
        this.sessionId = sessionId;
    }
}
