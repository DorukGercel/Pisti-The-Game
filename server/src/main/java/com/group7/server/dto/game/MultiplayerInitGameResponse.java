package com.group7.server.dto.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.definitions.common.StatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** DTO used for start new game responses*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MultiplayerInitGameResponse extends InitGameResponse{
    /** Username of the opponent player*/
    private String opponentUsername;

    /** All args constructor*/
    @JsonCreator
    public MultiplayerInitGameResponse(@JsonProperty("statusCode") StatusCode statusCode,
                                       @JsonProperty("errorMessage") String errorMessage,
                                       @JsonProperty("gameId") Long gameId,
                                       @JsonProperty("opponentUsername") String opponentUsername) {
        super(statusCode, errorMessage, gameId);
        this.opponentUsername = opponentUsername;
    }
}
