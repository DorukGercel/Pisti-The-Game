package com.group7.server.dto.leaderboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.group7.server.definitions.common.StatusCode;
import com.group7.server.definitions.leaderboard.RecordEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListRecordsResponse extends LeaderboardResponse {
    /** List of records according to query*/
    private List<RecordEntry> recordEntryList;

    /** All args constructor*/
    public ListRecordsResponse(@JsonProperty("statusCode") StatusCode statusCode,
                               @JsonProperty("errorMessage") String errorMessage,
                               @JsonProperty("recordEntryList") List<RecordEntry> recordEntryList) {
        super(statusCode, errorMessage);
        this.recordEntryList = new ArrayList<>();
        if (recordEntryList != null && recordEntryList.size() > 0) {
            // Make null safe
            this.recordEntryList.addAll(recordEntryList);
        }
    }
}
