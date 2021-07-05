package com.group7.server.dto.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.definitions.common.StatusCode;
import com.group7.server.definitions.game.Game;
import com.group7.server.definitions.game.GameEnvironment;
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
                                @JsonProperty("gameStatusCode") Game.GameStatusCode gameStatusCode) {
        super(statusCode, errorMessage);
        this.playerEnvironment = playerEnvironment;
        this.gameStatusCode = Game.GameStatusCode.convertGameStatusCodeToStr(gameStatusCode);
    }

}
