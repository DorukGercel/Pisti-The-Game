package com.group7.server.dto.game;

import com.group7.server.definitions.StatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/** DTO used for start new game responses*/
@EqualsAndHashCode(callSuper = true)
@Data
public class InitGameResponse extends GameResponse {
    /**Id of the game that is created with the request*/
    @NotEmpty
    private Long gameId;

    /** All args constructor*/
    public InitGameResponse(StatusCode statusCode, String errorMessage, Long gameId){
        super(statusCode, errorMessage);
        this.gameId = gameId;
    }
}
