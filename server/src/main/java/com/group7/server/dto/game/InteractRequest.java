package com.group7.server.dto.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.definitions.game.Game;
import com.group7.server.dto.common.CommonRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/** DTO used for interacting with the game*/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class InteractRequest extends CommonRequest {

    /** Session id of the active player who requests*/
    @NotEmpty
    private Long sessionId;

    /** Id of the game that is interacted with*/
    @NotEmpty
    private Long gameId;

    /** No of the card to be played*/
    @NotEmpty
    private Short cardNo;

    /** Type of the move to be simulated*/
    @NotEmpty
    private String moveType;

    /** Status of the game*/
    @NotEmpty
    private String gameStatusCode;

    /** All args constructor*/
    @JsonCreator
    public InteractRequest(@JsonProperty("sessionId") Long sessionId,
                           @JsonProperty("gameId") Long gameId,
                           @JsonProperty("cardNo") Short cardNo,
                           @JsonProperty("moveType") Game.MoveType moveType,
                           @JsonProperty("gameStatusCode") Game.GameStatusCode gameStatusCode) {
        this.sessionId = sessionId;
        this.gameId = gameId;
        this.cardNo = cardNo;

        switch (moveType) {
            case INITIAL -> this.moveType = "INITIAL";
            case CARD -> this.moveType = "CARD";
            case BLUFF -> this.moveType = "BLUFF";
            case CHALLENGE -> this.moveType = "CHALLENGE";
            case NOT_CHALLENGE -> this.moveType = "NOT_CHALLENGE";
            case REDEAL -> this.moveType = "REDEAL";
            case RESTART -> this.moveType = "RESTART";
        }

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
