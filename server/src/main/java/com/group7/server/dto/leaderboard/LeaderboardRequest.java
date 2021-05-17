package com.group7.server.dto.leaderboard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.dto.common.CommonRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/** DTO used for all type of record request; create, update, delete*/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class LeaderboardRequest extends CommonRequest {

    @NotEmpty
    private Long playerId;

    @NotEmpty
    private Date date;

    private Long recordId;

    private Integer score;


    @JsonCreator
    public LeaderboardRequest(@JsonProperty("playerId") Long playerId,
                              @JsonProperty("date") Date date,
                              @JsonProperty("id") Long recordId,
                              @JsonProperty("score") Integer score) {
        this.playerId = playerId;
        this.date = date;
        this.recordId = recordId;
        this.score = score;
    }
}
