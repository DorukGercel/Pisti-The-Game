package com.group7.server.service.leaderboard;

import com.group7.server.model.LeaderboardRecord;

import java.util.List;

/**
 * Responsible for providing utilities to the LeaderboardRecordController.
 *
 */
public interface LeaderboardRecordService {
    LeaderboardRecord createRecord(LeaderboardRecord record);
    LeaderboardRecord updateRecord(LeaderboardRecord record);
    void deleteRecord(LeaderboardRecord record);
    List<LeaderboardRecord> getAllTimeRecords();
    List<LeaderboardRecord> getWeeklyRecords();
    List<LeaderboardRecord> getMonthlyRecords();

}
