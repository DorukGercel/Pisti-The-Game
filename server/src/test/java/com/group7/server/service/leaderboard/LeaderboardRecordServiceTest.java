package com.group7.server.service.leaderboard;

import com.group7.server.model.LeaderboardRecord;
import com.group7.server.model.Player;
import com.group7.server.repository.LeaderboardRecordRepository;
import com.group7.server.repository.LeaderboardRecordRepositoryTestStub;
import com.group7.server.repository.PlayerRepository;
import com.group7.server.repository.PlayerRepositoryTestStub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    void setLeaderboardRecordService(LeaderboardRecordService leaderboardRecordService) {
        this.mLeaderboardRecordService = leaderboardRecordService;
    }

    @Test
    public void testCreateRecord() {
        // Create a leaderboard record with user who hasn't played a game before
        Player testPlayer = new Player("Rabia", "lolFriends", "r@g.com");
        Date testDate = new Date();
        LeaderboardRecord testRecord = new LeaderboardRecord(testPlayer, testDate, 500);
        LeaderboardRecord createdTestRecord = mLeaderboardRecordService.createRecord(testRecord);
        assertNotNull(createdTestRecord);
        // Create a leaderboard record with user who has already been in the leaderboard
        createdTestRecord = mLeaderboardRecordService.createRecord(testRecord);
        assertNull(createdTestRecord);
    }

    @Test
    public void testUpdateRecord() {
         // Create a leaderboard record
         Player testPlayer = new Player("Rabia", "lolFriends", "r@g.com");
         Date testDate = new Date();
         LeaderboardRecord testRecord = new LeaderboardRecord(testPlayer, testDate, 500);
         LeaderboardRecord createdTestRecord = mLeaderboardRecordService.createRecord(testRecord);
         assertNotNull(createdTestRecord);
         // Create a record to be used in updating
         Date updateDate = new Date();
         LeaderboardRecord updateWithRecord = new LeaderboardRecord(testPlayer, updateDate, 600);
         LeaderboardRecord updatedRecord = mLeaderboardRecordService.updateRecord(updateWithRecord);
         // Check if record correctly updated
         assertNotNull(updatedRecord);
         assertEquals(updatedRecord.getEndDate(), updateDate);
         assertEquals(updatedRecord.getScore(), 600);
    }

    @Test
    public void testDeleteRecord(){
        // Create a leaderboard record
        Player testPlayer = new Player("Rabia", "lolFriends", "r@g.com");
        Date testDate = new Date();
        LeaderboardRecord testRecord = new LeaderboardRecord(testPlayer, testDate, 500);
        LeaderboardRecord createdTestRecord = mLeaderboardRecordService.createRecord(testRecord);
        assertNotNull(createdTestRecord);
        mLeaderboardRecordService.deleteRecord(createdTestRecord);
    }

    @Test
    public void testGetRecordsByDate() {
        // Create player 1's leaderboard record
        Player testPlayer1 = new Player("test1", "test1123", "test1@t.com");
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, 2021);
        calendar1.set(Calendar.MONTH, 4);
        calendar1.set(Calendar.DATE, 27);
        Date date1 = calendar1.getTime();
        LeaderboardRecord record1 = new LeaderboardRecord(testPlayer1, date1, 200);
        // Create player 2's leaderboard record
        Player testPlayer2 = new Player("test2", "test2123", "test2@t.com");
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 2021);
        calendar2.set(Calendar.MONTH, 4);
        calendar2.set(Calendar.DATE, 28);
        Date date2 = calendar2.getTime();
        LeaderboardRecord record2 = new LeaderboardRecord(testPlayer2, date2, 300);
        // Create player 3's leaderboard record
        Player testPlayer3 = new Player("test3", "test3123", "test3@t.com");
        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(Calendar.YEAR, 2021);
        calendar3.set(Calendar.MONTH, 4);
        calendar3.set(Calendar.DATE, 7);
        Date date3 = calendar2.getTime();
        LeaderboardRecord record3 = new LeaderboardRecord(testPlayer3, date3, 400);
        // Create player 4's leaderboard record
        Player testPlayer4 = new Player("test4", "test4123", "test4@t.com");
        Calendar calendar4 = Calendar.getInstance();
        calendar3.set(Calendar.YEAR, 2021);
        calendar3.set(Calendar.MONTH, 3);
        calendar3.set(Calendar.DATE, 30);
        Date date4 = calendar2.getTime();
        LeaderboardRecord record4 = new LeaderboardRecord(testPlayer4, date4, 500);
        // Check if only record1 and record2 are retrieved
        List<LeaderboardRecord> weeklyRecords = mLeaderboardRecordService.getRecordsByDate("weekly");
        assertEquals(testPlayer2, weeklyRecords.get(0).getPlayer());
        assertEquals(testPlayer1, weeklyRecords.get(1).getPlayer());
        // Check if only record1, record2 and record3 are retrieved
        List<LeaderboardRecord> monthlyRecords = mLeaderboardRecordService.getRecordsByDate("monthly");
        assertEquals(testPlayer3, monthlyRecords.get(0).getPlayer());
        assertEquals(testPlayer2, monthlyRecords.get(1).getPlayer());
        assertEquals(testPlayer1, monthlyRecords.get(2).getPlayer());
        // Check if all four records are retrieved
        List<LeaderboardRecord> allTimesRecords = mLeaderboardRecordService.getRecordsByDate("allTimes");
        assertEquals(testPlayer4, allTimesRecords.get(0).getPlayer());
        assertEquals(testPlayer3, allTimesRecords.get(1).getPlayer());
        assertEquals(testPlayer2, allTimesRecords.get(2).getPlayer());
        assertEquals(testPlayer1, allTimesRecords.get(3).getPlayer());
    }






}
