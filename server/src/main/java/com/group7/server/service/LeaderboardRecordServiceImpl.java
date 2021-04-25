package com.group7.server.service;

import com.group7.server.model.LeaderboardRecord;
import com.group7.server.model.Player;
import com.group7.server.repository.LeaderboardRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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
    // TODO implement remaining methods
    @Override
    public List<LeaderboardRecord> getAllTimeRecords() {
        return null;
    }

    @Override
    public List<LeaderboardRecord> getWeeklyRecords() {
        return null;
    }

    @Override
    public List<LeaderboardRecord> getMonthlyRecords() {
        return null;
    }
}
