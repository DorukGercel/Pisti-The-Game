package com.group7.server.service.leaderboard;

import com.group7.server.definitions.common.StatusCode;
import com.group7.server.definitions.leaderboard.RecordEntry;
import com.group7.server.model.Player;
import com.group7.server.model.LeaderboardRecord;
import com.group7.server.repository.LeaderboardRecordRepository;
import com.group7.server.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private final PlayerRepository            mPlayerRepository;

    /**
     * Creates a record in the leaderboard record table.
     *
     * @param playerId the player id of the record.
     * @param date the date record belongs to.
     * @param score to be set.
     * @return return the status code of the operation
     */
    @Override
    public StatusCode createRecord(Long playerId, Date date, Short score) {
        try {
            Optional<Player> dbPlayer = mPlayerRepository.findById(playerId);
            if(dbPlayer.isEmpty()) {
                return StatusCode.FAIL;
            }
            LeaderboardRecord record = new LeaderboardRecord(dbPlayer.get(), date, score);
            List<LeaderboardRecord> records = mLeaderboardRecordRepository.findAll();
            if(records.size() == 0) { // very first record in the leaderboard
                mLeaderboardRecordRepository.save(record);
                return StatusCode.SUCCESS;
            } else {
                for(int i = 0; i < records.size(); i++){
                    if(!record.getPlayer().getId().equals(records.get(i).getPlayer().getId())) {
                        // If the player is not already in the leaderboard, save it.
                        mLeaderboardRecordRepository.save(record);
                        return StatusCode.SUCCESS;
                    } else {
                        // The player is already in the leaderboard!
                        return StatusCode.FAIL;
                    }
                }
            }
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.FAIL;
        }
    }

    /**
     * Updates a record in the leaderboard record table.
     *
     * @param playerId the player id of the record.
     * @param date the date record belongs to.
     * @param score to be set.
     * @return return the status code of the operation.
     */

    @Override
    public StatusCode updateRecord(Long recordId, Long playerId, Date date, Short score) {
        try {
            LeaderboardRecord record = findRecord(recordId);
            if (record == null) {
                return StatusCode.FAIL;
            }
            if (mPlayerRepository.findById(playerId).isEmpty()){
                return StatusCode.FAIL;
            }
            record.setPlayer(mPlayerRepository.findById(playerId).get());
            record.setScore(score);
            record.setEndDate(date);
            mLeaderboardRecordRepository.save(record);
            return StatusCode.SUCCESS;
        } catch(java.util.NoSuchElementException e) {
            e.printStackTrace();
            return StatusCode.FAIL;
        }
    }

    /**
     * Deletes a record in the leaderboard record table.
     *
     * @param recordId the id of the record.
     *
     * @return the status code of the operation
     */
    @Override
    public StatusCode deleteRecord(Long recordId) {
        try {
            LeaderboardRecord record = findRecord(recordId);
            if (record == null) {
                return StatusCode.FAIL;
            }
            mLeaderboardRecordRepository.deleteById(record.getId());
            return StatusCode.SUCCESS;
        } catch(Exception e) {
            return StatusCode.FAIL;
        }
    }

    /**
     * Retrieve all records according to given period from the leaderboard record table.
     *
     * @param period it can be "weekly", "monthly" or "allTimes"
     * @param recordEntryList List of leaderboard records
     *
     * return the status code of the operation
     */
    @Override
    public StatusCode getRecordsByDate(Period period, List<RecordEntry> recordEntryList) {
        try {
            if (recordEntryList == null || period == null){
                return StatusCode.FAIL;
            }
            Date now = new java.util.Date();
            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String nowDate = formatter.format(now);
            if (period.equals(Period.WEEKLY)) {
                Date sevenDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(7)));
                String sevenDaysAgoDate = formatter.format(sevenDaysAgo);
                List<LeaderboardRecord> dbLeaderboardRecords = mLeaderboardRecordRepository
                        .findByEndDateBetween(
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(sevenDaysAgoDate),
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(nowDate));
                // TODO: Remove print
                //System.out.println(nowDate);
                recordEntryList.addAll(
                    convertToRecordList(dbLeaderboardRecords)
                );
                // TODO: Remove print
                //System.out.println(recordEntryList);
                //System.out.println(dbLeaderboardRecords);
            } else if (period.equals(Period.MONTHLY)) {
                Date thirtyDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(30)));
                String thirtyDaysAgoDate = formatter.format(thirtyDaysAgo);
                List<LeaderboardRecord> dbLeaderboardRecords = mLeaderboardRecordRepository
                        .findByEndDateBetween(
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(thirtyDaysAgoDate),
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(nowDate));
                recordEntryList.addAll(
                        convertToRecordList(dbLeaderboardRecords)
                );
                // TODO: Remove print
                //System.out.println(recordEntryList);
                //System.out.println(dbLeaderboardRecords);
            } else {
                /* period == "allTimes" */
                List<LeaderboardRecord> dbLeaderboardRecords = mLeaderboardRecordRepository.findAll();
                recordEntryList.addAll(convertToRecordList(dbLeaderboardRecords));
                // TODO: Remove print
                //System.out.println(recordEntryList);
                //System.out.println(dbLeaderboardRecords);
            }
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            System.out.println("Exception occurred during Date parsing");
            e.printStackTrace();
            return StatusCode.FAIL;
        }
    }

    /** Helper function to find record*/
    private LeaderboardRecord findRecord(Long recordId) {
        Optional<LeaderboardRecord> dbRecord = mLeaderboardRecordRepository.findById(recordId);
        if(dbRecord.isEmpty()) {
            return null;
        }
        return dbRecord.get();
    }

    /** Helper function to convert from leaderboard record to record list*/
    private List<RecordEntry> convertToRecordList(List<LeaderboardRecord> leaderboardRecordList) {
        List<RecordEntry> recordEntryList = new ArrayList<>();
        for(LeaderboardRecord leaderboardRecord : leaderboardRecordList) {
            recordEntryList.add(new RecordEntry(leaderboardRecord.getPlayer().getUsername(),
                                      leaderboardRecord.getScore(),
                                      leaderboardRecord.getEndDate()
                    )
            );
        }
        return recordEntryList;
    }

}
