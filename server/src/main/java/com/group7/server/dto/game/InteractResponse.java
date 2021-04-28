package com.group7.server.dto.game;

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

    public InteractResponse(StatusCode statusCode, String errorMessage, GameEnvironment playerEnvironment, GameEnvironment pcEnvironment) {
        super(statusCode, errorMessage);
        this.playerEnvironment = playerEnvironment;
        this.pcEnvironment = pcEnvironment;
    }


}
