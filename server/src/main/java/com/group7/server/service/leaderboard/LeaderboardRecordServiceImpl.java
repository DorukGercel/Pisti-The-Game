package com.group7.server.service.leaderboard;

import com.group7.server.model.Player;
import com.group7.server.model.LeaderboardRecord;
import com.group7.server.repository.LeaderboardRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        for(int i = 0; i< players.size(); i++){
            if(record.getPlayer().getId() != players.get(i).getId()) {
                return mLeaderboardRecordRepository.save(record);
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
     * Retrieve all records from the leaderboard record table.
     *
     * @return list of all leaderboard records
     */
    @Override
    public List<LeaderboardRecord> getAllTimeRecords() {
        return mLeaderboardRecordRepository.findAll();
    }

    /**
     * Retrieve all records in the last 7 days from the leaderboard record table.
     *
     * @return List of the last 7 days' leaderboard records
     *              If exception does not occur, returns List of LeaderboardRecord objects.
     *              If exception does not occurs, returns null.
     */
    @Override
    public List<LeaderboardRecord> getWeeklyRecords() {
        try {
            Date now = new java.util.Date();
            Date sevenDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(7)));
            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowDate = formatter.format(now);
            String sevenDaysAgoDate = formatter.format(sevenDaysAgo);
            List<LeaderboardRecord> last7daysRecords = mLeaderboardRecordRepository.findByEndDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(nowDate),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(sevenDaysAgoDate));
            return last7daysRecords;
        } catch (Exception e) {
            System.out.println("Exception occurred during Date parsing");
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Retrieve all records in the last 30 days from the leaderboard record table.
     *
     * @return List of the last 30 days' leaderboard records
     *              If exception does not occur, returns List of LeaderboardRecord objects.
     *              If exception does not occurs, returns null.
     */
    @Override
    public List<LeaderboardRecord> getMonthlyRecords() {
        try {
            Date now = new java.util.Date();
            Date thirtyDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(30)));
            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowDate = formatter.format(now);
            String thirtyDaysAgoDate = formatter.format(thirtyDaysAgo);
            List<LeaderboardRecord> last30daysRecords = mLeaderboardRecordRepository.findByEndDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(nowDate),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(thirtyDaysAgoDate));
            return last30daysRecords;
        } catch (Exception e) {
            System.out.println("Exception occurred during Date parsing");
            e.printStackTrace();
            return null;
        }
    }
}
