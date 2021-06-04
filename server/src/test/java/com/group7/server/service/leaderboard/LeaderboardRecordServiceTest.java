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

    //In this test case change date of user's
    // TODO: Fix this test case dates
    @Test
    public void testGetRecordsByDate() {
        // Create player 1's leaderboard record
        Player testPlayer1 = new Player("test1", "test1123", "test1@t.com");
        testPlayer1.setId(1L);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, 2021);
        calendar1.set(Calendar.MONTH, 3);
        calendar1.set(Calendar.DATE, 27);
        Date date1 = calendar1.getTime();
        LeaderboardRecord record1 = new LeaderboardRecord(testPlayer1, date1, 200);
        mLeaderboardRecordRepository.save(record1);
        // Create player 2's leaderboard record
        Player testPlayer2 = new Player("test2", "test2123", "test2@t.com");
        testPlayer2.setId(2L);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 2021);
        calendar2.set(Calendar.MONTH, 3);
        calendar2.set(Calendar.DATE, 28);
        Date date2 = calendar2.getTime();
        LeaderboardRecord record2 = new LeaderboardRecord(testPlayer2, date2, 300);
        mLeaderboardRecordRepository.save(record2);
        // Create player 3's leaderboard record
        Player testPlayer3 = new Player("test3", "test3123", "test3@t.com");
        testPlayer3.setId(3L);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(Calendar.YEAR, 2021);
        calendar3.set(Calendar.MONTH, 3);
        calendar3.set(Calendar.DATE, 7);
        Date date3 = calendar3.getTime();
        LeaderboardRecord record3 = new LeaderboardRecord(testPlayer3, date3, 400);
        mLeaderboardRecordRepository.save(record3);
        // Create player 4's leaderboard record
        Player testPlayer4 = new Player("test4", "test4123", "test4@t.com");
        testPlayer4.setId(4L);
        Calendar calendar4 = Calendar.getInstance();
        calendar4.set(Calendar.YEAR, 2021);
        calendar4.set(Calendar.MONTH, 2);
        calendar4.set(Calendar.DATE, 15);
        Date date4 = calendar4.getTime();
        LeaderboardRecord record4 = new LeaderboardRecord(testPlayer4, date4, 500);
        mLeaderboardRecordRepository.save(record4);
        // Check if only record1 and record2 are retrieved
        List<RecordEntry> weeklyRecords = new ArrayList<>();
        /*mLeaderboardRecordService.getRecordsByDate(LeaderboardRecordService.Period.WEEKLY, weeklyRecords);
        assertEquals(testPlayer2.getUsername(), weeklyRecords.get(0).getPlayerName());
        assertEquals(testPlayer1.getUsername(), weeklyRecords.get(1).getPlayerName());*/
        // Check if only record1, record2 and record3 are retrieved
        List<RecordEntry> monthlyRecords = new ArrayList<>();
        mLeaderboardRecordService.getRecordsByDate(LeaderboardRecordService.Period.MONTHLY, monthlyRecords);
        assertEquals(testPlayer3.getUsername(), monthlyRecords.get(0).getPlayerName());
        assertEquals(testPlayer2.getUsername(), monthlyRecords.get(1).getPlayerName());
        assertEquals(testPlayer1.getUsername(), monthlyRecords.get(2).getPlayerName());
        // Check if all four records are retrieved
        List<RecordEntry> allTimesRecords = new ArrayList<>();
        mLeaderboardRecordService.getRecordsByDate(LeaderboardRecordService.Period.ALL_TIMES, allTimesRecords);
        assertEquals(testPlayer4.getUsername(), allTimesRecords.get(0).getPlayerName());
        assertEquals(testPlayer3.getUsername(), allTimesRecords.get(1).getPlayerName());
        assertEquals(testPlayer2.getUsername(), allTimesRecords.get(2).getPlayerName());
        assertEquals(testPlayer1.getUsername(), allTimesRecords.get(3).getPlayerName());
    }






}
