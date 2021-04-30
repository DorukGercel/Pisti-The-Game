package com.group7.server.service.leaderboard;

import com.group7.server.definitions.common.StatusCode;
import com.group7.server.model.LeaderboardRecord;

import java.util.HashMap;
import java.util.List;

/**
 * Responsible for providing utilities to the LeaderboardRecordController.
 *
 */
public interface LeaderboardRecordService {
    LeaderboardRecord createRecord(LeaderboardRecord record);
    LeaderboardRecord updateRecord(LeaderboardRecord record);
    StatusCode deleteRecord(LeaderboardRecord record);
    StatusCode getRecordsByDate(Period period, List<LeaderboardRecord> leaderboardRecordList);

     enum Period {
         ALL_TIMES,
         WEEKLY,
         MONTHLY
     }
}
