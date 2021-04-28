package com.group7.server.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/** DTO used for interacting with the game*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InteractRequest {

    /** Session id of the active player who requests*/
    @NotEmpty
    private Long sessionId;

    /** Id of the game that is interacted with*/
    @NotEmpty
    private Long gameId;

    /**
     * No of the card to be played;
     * Attention: if < 0 then it is initial request
     **/
    @NotEmpty
    private Short cardNo;
}
