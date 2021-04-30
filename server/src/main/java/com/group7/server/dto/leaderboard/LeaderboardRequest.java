package com.group7.server.dto.leaderboard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.model.LeaderboardRecord;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/** DTO used for all type of record request; create, update, delete*/
@Data
@NoArgsConstructor
public class LeaderboardRequest {
    /** Record to be deleted*/
    @NotEmpty
    private LeaderboardRecord leaderboardRecord;

    @JsonCreator
    public LeaderboardRequest(@JsonProperty("leaderboardRecord") LeaderboardRecord leaderboardRecord) {
        this.leaderboardRecord = leaderboardRecord;
    }
}
