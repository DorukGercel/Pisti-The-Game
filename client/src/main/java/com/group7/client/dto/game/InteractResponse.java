package com.group7.client.dto.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.game.GameEnvironment;
import com.group7.client.definitions.game.GameStatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
                            @JsonProperty("gameStatusCode") GameStatusCode gameStatusCode) {
        super(statusCode, errorMessage);
        this.playerEnvironment = playerEnvironment;
        this.pcEnvironment = pcEnvironment;

        switch (gameStatusCode) {
            case NORMAL -> this.gameStatusCode = "NORMAL";
            case LEVEL_UP -> this.gameStatusCode = "LEVEL_UP";
            case CHEAT_LEVEL_UP -> this.gameStatusCode = "CHEAT_LEVEL_UP";
            case WIN -> this.gameStatusCode = "WIN";
            case LOST -> this.gameStatusCode = "LOST";
            case GAME_OVER_WIN -> this.gameStatusCode = "GAME_OVER_WIN";
        }
    }


}
