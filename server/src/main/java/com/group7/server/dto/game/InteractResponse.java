package com.group7.server.dto.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.definitions.GameEnvironment;
import com.group7.server.definitions.StatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class InteractResponse extends GameResponse {

    @NotEmpty
    private GameEnvironment playerEnvironment;

    @NotEmpty
    private GameEnvironment pcEnvironment;

    @JsonCreator
    public InteractResponse(@JsonProperty("statusCode") StatusCode statusCode,
                            @JsonProperty("errorMessage") String errorMessage,
                            @JsonProperty("playerEnvironment") GameEnvironment playerEnvironment,
                            @JsonProperty("pcEnvironment") GameEnvironment pcEnvironment) {
        super(statusCode, errorMessage);
        this.playerEnvironment = playerEnvironment;
        this.pcEnvironment = pcEnvironment;
    }


}
