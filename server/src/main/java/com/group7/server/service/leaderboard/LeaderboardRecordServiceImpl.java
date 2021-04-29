package com.group7.server.service.leaderboard;

import com.group7.server.model.Player;
import com.group7.server.model.LeaderboardRecord;
import com.group7.server.repository.LeaderboardRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.time.Duration;
import java.time.Instant;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Responsible for providing utilities to the LeaderboardRecordController.
 *
 */
@RequiredArgsConstructor
@Service
public class LeaderboardRecordServiceImpl implements LeaderboardRecordService {

    private final LeaderboardRecordRepository mLeaderboardRecordRepository;

    /**
     * Creates a record in the leaderboard record table.
     *
     * @param record the record which needs to be added to the leaderboard record table.
     * @return created Leaderboard Record object.
     *              If the player of given record is already in the leaderboard; returns null.
     */
    @Override
    public LeaderboardRecord createRecord(LeaderboardRecord record) {
        List<Player> players = mLeaderboardRecordRepository.getAllPlayers();
        if(players.size() == 0) { // very first record in the leaderboard
            mLeaderboardRecordRepository.save(record);
        } else {
            for(int i = 0; i < players.size(); i++){
                if(!record.getPlayer().getId().equals(players.get(i).getId())) {
                    return mLeaderboardRecordRepository.save(record);
                }
            }
        }
        return null;
    }

    /**
     * Updates a record in the leaderboard record table.
     *
     * @param record the record which needs to be updated.
     * @return LeaderboardRecord object.
     *              If the record exists, returns updated LeaderboardRecord object.
     *               If the record does not exist, returns null.
     */

    @Override
    public LeaderboardRecord updateRecord(LeaderboardRecord record) {
        try {
            Optional<LeaderboardRecord> dbRecord = mLeaderboardRecordRepository.findById(record.getId());
            LeaderboardRecord dbRecordObj = dbRecord.get();
            dbRecordObj.setScore(record.getScore());
            dbRecordObj.setEndDate(record.getEndDate());
            return mLeaderboardRecordRepository.save(dbRecordObj);
        } catch(java.util.NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Deletes a record in the leaderboard record table.
     *
     * @param record the record to be deleted.
     */
    @Override
    public void deleteRecord(LeaderboardRecord record) {
        try {
            mLeaderboardRecordRepository.deleteById(record.getId());
        } catch(Exception e) {
            return;
        }
    }

    /**
     * Retrieve all records according to given period from the leaderboard record table.
     *
     * @param period it can be "weekly", "monthly" or "allTimes"
     * @return List of leaderboard records
     *              If exception does not occur, returns List of LeaderboardRecord objects.
     *              If exception does not occurs, returns null.
     */
    @Override
    public List<LeaderboardRecord> getRecordsByDate(String period) {
        try {
            List<LeaderboardRecord> records;
            Date now = new java.util.Date();
            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:");
            String nowDate = formatter.format(now);
            if (period.equals("weekly")) {
                Date sevenDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(7)));
                String sevenDaysAgoDate = formatter.format(sevenDaysAgo);
                records = mLeaderboardRecordRepository.findByEndDateBetween(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(nowDate),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(sevenDaysAgoDate));
            } else if (period.equals("monthly")) {
                Date thirtyDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(30)));
                String thirtyDaysAgoDate = formatter.format(thirtyDaysAgo);
                records = mLeaderboardRecordRepository.findByEndDateBetween(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(nowDate),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(thirtyDaysAgoDate));
            } else {
                /* period == "allTimes" */
                records = mLeaderboardRecordRepository.findAll();
            }
            return records;
        } catch (Exception e) {
            System.out.println("Exception occurred during Date parsing");
            e.printStackTrace();
            return null;
        }
    }


}
