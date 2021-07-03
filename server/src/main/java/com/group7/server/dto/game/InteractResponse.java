package com.group7.server.dto.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.definitions.game.Game;
import com.group7.server.definitions.game.GameEnvironment;
import com.group7.server.definitions.common.StatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class InteractResponse extends GameResponse {
    private GameEnvironment playerEnvironment;

    private GameEnvironment pcEnvironment;

    /** Status of the game*/
    @NotEmpty
    private String gameStatusCode;

    @JsonCreator
    public InteractResponse(@JsonProperty("statusCode") StatusCode statusCode,
                            @JsonProperty("errorMessage") String errorMessage,
                            @JsonProperty("playerEnvironment") GameEnvironment playerEnvironment,
                            @JsonProperty("pcEnvironment") GameEnvironment pcEnvironment,
                            @JsonProperty("gameStatusCode") Game.GameStatusCode gameStatusCode) {
        super(statusCode, errorMessage);
        this.playerEnvironment = playerEnvironment;
        this.pcEnvironment = pcEnvironment;
        this.gameStatusCode = Game.GameStatusCode.convertGameStatusCodeToStr(gameStatusCode);
    }


}
