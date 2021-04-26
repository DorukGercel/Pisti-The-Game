package com.group7.server.dto.game;

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
    public GameResponse(StatusCode statusCode, String errorMessage){
        super(statusCode, errorMessage);
    }
}
