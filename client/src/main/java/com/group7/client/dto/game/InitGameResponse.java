package com.group7.client.dto.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.client.definitions.common.StatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/** DTO used for start new game responses*/
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class InitGameResponse extends GameResponse {
    /** Id of the game that is created with the request*/
    @NotNull
    private Long gameId;

    /** All args constructor*/
    @JsonCreator
    public InitGameResponse(@JsonProperty("statusCode") StatusCode statusCode,
                            @JsonProperty("errorMessage") String errorMessage,
                            @JsonProperty("gameId") Long gameId){
        super(statusCode, errorMessage);
        this.gameId = gameId;
    }
}
