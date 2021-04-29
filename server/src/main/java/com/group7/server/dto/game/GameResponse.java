package com.group7.server.dto.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.definitions.StatusCode;
import com.group7.server.dto.common.CommonResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** DTO used as a super class for game related responses*/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class GameResponse extends CommonResponse {

    /** All args constructor*/
    @JsonCreator
    public GameResponse(@JsonProperty("statusCode") StatusCode statusCode,
                        @JsonProperty("statusCode") String errorMessage){
        super(statusCode, errorMessage);
    }
}
