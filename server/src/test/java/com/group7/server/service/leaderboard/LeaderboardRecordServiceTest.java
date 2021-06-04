package com.group7.server.service.leaderboard;

import com.group7.server.definitions.common.StatusCode;
import com.group7.server.definitions.leaderboard.RecordEntry;
import com.group7.server.model.LeaderboardRecord;
import com.group7.server.model.Player;
import com.group7.server.repository.LeaderboardRecordRepository;
import com.group7.server.repository.LeaderboardRecordRepositoryTestStub;
import com.group7.server.repository.PlayerRepository;
import com.group7.server.repository.PlayerRepositoryTestStub;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        PlayerRepositoryTestStub.class,
        LeaderboardRecordRepositoryTestStub.class,
        LeaderboardRecordServiceImpl.class,
})
@WebAppConfiguration
public class LeaderboardRecordServiceTest {

    private LeaderboardRecordService mLeaderboardRecordService;
    private LeaderboardRecordRepository mLeaderboardRecordRepository;

    @Autowired
    void setLeaderboardRecordService(LeaderboardRecordService leaderboardRecordService, LeaderboardRecordRepository leaderboardRecordRepository) {
        this.mLeaderboardRecordService = leaderboardRecordService;
        this.mLeaderboardRecordRepository = leaderboardRecordRepository;
    }

    @After
    public void teardown(){
        mLeaderboardRecordRepository.deleteAll();
    }

    @Test
    public void testCreateRecord() {
        // Create a leaderboard record with user who hasn't played a game before
        Player testPlayer = new Player("Rabia", "lolFriends", "r@g.com");
        testPlayer.setId(1L);
        Date testDate = new Date();
        StatusCode statusCode = mLeaderboardRecordService.createRecord(testPlayer.getId(), testDate, (short) 500);
        assertEquals(statusCode, StatusCode.SUCCESS);
        // Create a leaderboard record with user who has already been in the leaderboard
        statusCode = mLeaderboardRecordService.createRecord(testPlayer.getId(), testDate, (short) 500);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testUpdateRecord() {
         // Create a leaderboard record
         Player testPlayer = new Player("Rabia", "lolFriends", "r@g.com");
         testPlayer.setId(1L);
         Date testDate = new Date();
         LeaderboardRecord testRecord = new LeaderboardRecord(testPlayer, testDate, 500);
         StatusCode statusCode = mLeaderboardRecordService.createRecord(testPlayer.getId(), testDate, (short) 500);
         assertEquals(statusCode, StatusCode.SUCCESS);
         // Create a record to be used in updating
         Date updateDate = new Date();
         statusCode = mLeaderboardRecordService.updateRecord(1L, testPlayer.getId(), updateDate, (short) 600);
         assertEquals(statusCode, StatusCode.SUCCESS);
    }

    @Test
    public void testDeleteRecord() {
        // Create a leaderboard record
        Player testPlayer = new Player("Rabia", "lolFriends", "r@g.com");
        testPlayer.setId(1L);
        Date testDate = new Date();
        StatusCode statusCode = mLeaderboardRecordService.createRecord(testPlayer.getId(), testDate, (short) 500);
        assertEquals(statusCode, StatusCode.SUCCESS);
        // Delete an existing record
        statusCode = mLeaderboardRecordService.deleteRecord(1L);
        assertEquals(statusCode, StatusCode.SUCCESS);
        // Can't delete already deleted record
        statusCode = mLeaderboardRecordService.deleteRecord(1L);
        assertEquals(statusCode, StatusCode.FAIL);
    }

}
