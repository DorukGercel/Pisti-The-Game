package com.group7.server.service.leaderboard;

import com.group7.server.definitions.common.StatusCode;
import com.group7.server.model.LeaderboardRecord;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Responsible for providing utilities to the LeaderboardRecordController.
 *
 */
public interface LeaderboardRecordService {
    StatusCode createRecord(Long playerId, Date date, Integer score);
    StatusCode updateRecord(Long recordId, Long playerId, Date date, Integer score);
    StatusCode deleteRecord(Long recordId);
    StatusCode getRecordsByDate(Period period, List<LeaderboardRecord> leaderboardRecordList);

     enum Period {
         ALL_TIMES,
         WEEKLY,
         MONTHLY
     }
}
