package com.group7.server.service.leaderboard;

import com.group7.server.definitions.common.StatusCode;
import com.group7.server.definitions.leaderboard.RecordEntry;

import java.util.Date;
import java.util.List;

/**
 * Responsible for providing utilities to the LeaderboardRecordController.
 *
 */
public interface LeaderboardRecordService {
    StatusCode createRecord(Long playerId, Date date, Short score);
    StatusCode updateRecord(Long recordId, Long playerId, Date date, Short score);
    StatusCode deleteRecord(Long recordId);
    StatusCode getRecordsByDate(Period period, List<RecordEntry> recordEntryList);

     enum Period {
         ALL_TIMES,
         WEEKLY,
         MONTHLY
     }
}
