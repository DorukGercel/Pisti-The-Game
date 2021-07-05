package com.group7.client.dto.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.game.GameEnvironment;
import com.group7.client.definitions.game.GameStatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class MultiplayerInteractResponse extends GameResponse {
    private GameEnvironment playerEnvironment;

    /** Status of the game*/
    @NotEmpty
    private String gameStatusCode;

    @JsonCreator
    public MultiplayerInteractResponse(@JsonProperty("statusCode") StatusCode statusCode,
                                       @JsonProperty("errorMessage") String errorMessage,
                                       @JsonProperty("playerEnvironment") GameEnvironment playerEnvironment,
                                       @JsonProperty("gameStatusCode") GameStatusCode gameStatusCode) {
        super(statusCode, errorMessage);
        this.playerEnvironment = playerEnvironment;
        this.gameStatusCode = GameStatusCode.convertGameStatusCodeToStr(gameStatusCode);
    }

}
