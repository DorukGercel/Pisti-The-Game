package com.group7.client.dto.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.client.definitions.game.MoveType;
import com.group7.client.dto.common.CommonRequest;
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

    /** All args constructor*/
    @JsonCreator
    public InteractRequest(@JsonProperty("sessionId") Long sessionId,
                           @JsonProperty("gameId") Long gameId,
                           @JsonProperty("cardNo") Short cardNo,
                           @JsonProperty("moveType") MoveType moveType) {
        this.sessionId = sessionId;
        this.gameId = gameId;
        this.cardNo = cardNo;

        switch (moveType) {
            case INITIAL -> {
                this.moveType = "INITIAL";
            }
            case CARD -> {
                this.moveType = "CARD";
            }
            case REDEAL -> {
                this.moveType = "REDEAL";
            }
        }
    }
}
