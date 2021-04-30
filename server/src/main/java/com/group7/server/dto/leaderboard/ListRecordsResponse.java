package com.group7.server.dto.leaderboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.definitions.common.StatusCode;
import com.group7.server.model.LeaderboardRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListRecordsResponse extends LeaderboardResponse {
    /** List of records according to query*/
    private List<LeaderboardRecord> mLeaderboardRecordList;

    /** All args constructor*/
    public ListRecordsResponse(@JsonProperty("statusCode") StatusCode statusCode,
                               @JsonProperty("errorMessage") String errorMessage,
                               @JsonProperty("recordList") List<LeaderboardRecord> leaderboardRecordList) {
        super(statusCode, errorMessage);
        mLeaderboardRecordList = new ArrayList<>();
        mLeaderboardRecordList.addAll(leaderboardRecordList);
    }
}
