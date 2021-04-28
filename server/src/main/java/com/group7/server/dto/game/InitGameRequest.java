package com.group7.server.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/** DTO used for start new game requests*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitGameRequest {
    /** Session id of the active player who requests to start new game*/
    @NotEmpty
    private Long sessionId;
}
