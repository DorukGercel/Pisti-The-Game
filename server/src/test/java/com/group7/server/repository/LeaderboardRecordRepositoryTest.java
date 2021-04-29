package com.group7.server.repository;

import com.group7.server.model.LeaderboardRecord;
import com.group7.server.model.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LeaderboardRecordRepositoryTest {

    private LeaderboardRecordRepository mLeaderboardRecordRepository;
    private PlayerRepository mPlayerRepository;

    @Autowired
    public void setLeaderboardRecordRepository(
            LeaderboardRecordRepository leaderboardRecordRepository,
            PlayerRepository playerRepository) {
        this.mLeaderboardRecordRepository = leaderboardRecordRepository;
        this.mPlayerRepository = playerRepository;
    }

    @Test
    public void testCreateLeaderboardRecord() {
        // Create a new player
        Player testPlayer = new Player("test", "test", "test@t.com");
        Player savedPlayer = mPlayerRepository.save(testPlayer);
        // Create a new LeaderboardRecord
        Date date = new Date();
        LeaderboardRecord testRecord = new LeaderboardRecord(testPlayer, date, 300);
        LeaderboardRecord savedRecord = mLeaderboardRecordRepository.save(testRecord);
        assertNotNull(savedRecord);
    }

    @Test
    public void testReadLeaderboardRecord() {
        // Create a new player
        Player testPlayer = new Player("test", "test123", "test@t.com");
        Player savedPlayer = mPlayerRepository.save(testPlayer);
        // Create a new LeaderboardRecord
        Date date = new Date();
        LeaderboardRecord testRecord = new LeaderboardRecord(savedPlayer, date, 300);
        LeaderboardRecord savedRecord = mLeaderboardRecordRepository.save(testRecord);
        // Read by ID
        Optional<LeaderboardRecord> dbRecord = mLeaderboardRecordRepository.findById(savedRecord.getId());
        assertFalse(dbRecord.isEmpty());
        // Read by player
        dbRecord = mLeaderboardRecordRepository.findByPlayer(savedPlayer);
        assertFalse(dbRecord.isEmpty());
    }

    @Test
    public void testUpdateLeaderboardRecord() {
        // Create a new player
        Player testPlayer = new Player("test", "test123", "test@t.com");
        Player savedPlayer = mPlayerRepository.save(testPlayer);
        // Create a new LeaderboardRecord
        Date date = new Date();
        LeaderboardRecord testRecord = new LeaderboardRecord(savedPlayer, date, 300);
        LeaderboardRecord savedRecord = mLeaderboardRecordRepository.save(testRecord);
        Optional<LeaderboardRecord> dbRecord = mLeaderboardRecordRepository.findById(savedRecord.getId());
        // Check db record was saved
        assertTrue(dbRecord.isPresent());
        // Update score
        dbRecord.get().setScore(400);
        dbRecord.get().setEndDate(dbRecord.get().getEndDate());
        dbRecord.get().setPlayer(dbRecord.get().getPlayer());
        LeaderboardRecord updatedRecord = mLeaderboardRecordRepository.save(dbRecord.get());
        // Check score changed other parts unchanged
        assertEquals(400, updatedRecord.getScore());
        assertEquals(date, updatedRecord.getEndDate());
        assertEquals(savedPlayer, updatedRecord.getPlayer());
    }

    @Test
    public void testDeleteLeaderboardRecord() {
        Player testPlayer = new Player("test", "test123", "test@t.com");
        Player savedPlayer = mPlayerRepository.save(testPlayer);
        Date date = new Date();
        LeaderboardRecord testRecord = new LeaderboardRecord(savedPlayer, date, 300);
        LeaderboardRecord savedRecord = mLeaderboardRecordRepository.save(testRecord);
        Optional<LeaderboardRecord> dbRecord = mLeaderboardRecordRepository.findById(savedRecord.getId());
        // Check db record was saved
        assertTrue(dbRecord.isPresent());
        // Delete by id
        mLeaderboardRecordRepository.deleteById(dbRecord.get().getId());
        Optional<LeaderboardRecord> deletedRecord = mLeaderboardRecordRepository.findById(savedRecord.getId());
        // Check deletion
        assertTrue(deletedRecord.isEmpty());
    }

    @Test
    public void testWeeklyLeaderBoardRecords() {
        // Create player 1's leaderboard record
        Player testPlayer1 = new Player("test1", "test1123", "test1@t.com");
        Player savedPlayer1 = mPlayerRepository.save(testPlayer1);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, 2021);
        calendar1.set(Calendar.MONTH, 4);
        calendar1.set(Calendar.DATE, 27);
        Date date1 = calendar1.getTime();
        LeaderboardRecord testRecord1 = new LeaderboardRecord(savedPlayer1, date1, 200);
        LeaderboardRecord savedRecord1 = mLeaderboardRecordRepository.save(testRecord1);
        Optional<LeaderboardRecord> dbRecord1 = mLeaderboardRecordRepository.findById(savedRecord1.getId());
        // Create player 2's leaderboard record
        Player testPlayer2 = new Player("test2", "test2123", "test2@t.com");
        Player savedPlayer2 = mPlayerRepository.save(testPlayer2);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 2021);
        calendar2.set(Calendar.MONTH, 4);
        calendar2.set(Calendar.DATE, 28);
        Date date2 = calendar2.getTime();
        LeaderboardRecord testRecord2 = new LeaderboardRecord(savedPlayer2, date2, 300);
        LeaderboardRecord savedRecord2 = mLeaderboardRecordRepository.save(testRecord2);
        Optional<LeaderboardRecord> dbRecord2 = mLeaderboardRecordRepository.findById(savedRecord2.getId());
        // Create player 3's leaderboard record
        Player testPlayer3 = new Player("test3", "test3123", "test3@t.com");
        Player savedPlayer3 = mPlayerRepository.save(testPlayer3);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(Calendar.YEAR, 2021);
        calendar3.set(Calendar.MONTH, 4);
        calendar3.set(Calendar.DATE, 17);
        Date date3 = calendar2.getTime();
        LeaderboardRecord testRecord3 = new LeaderboardRecord(savedPlayer3, date3, 400);
        LeaderboardRecord savedRecord3 = mLeaderboardRecordRepository.save(testRecord3);
        Optional<LeaderboardRecord> dbRecord3 = mLeaderboardRecordRepository.findById(savedRecord3.getId());
        // Check db record was saved
        assertTrue(dbRecord1.isPresent());
        assertTrue(dbRecord2.isPresent());
        assertTrue(dbRecord3.isPresent());
        // Create the recent week's leaderboard records List
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date sevenDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(7)));
        String sevenDaysAgoDate = formatter.format(sevenDaysAgo);
        Date now = new java.util.Date();
        String nowDate = formatter.format(now);
        try{
            Optional<List<LeaderboardRecord>> dbRecords = Optional.ofNullable(mLeaderboardRecordRepository.findByEndDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(nowDate),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(sevenDaysAgoDate)));
            // Check db records saved
            assertTrue(dbRecords.isPresent());
            assertFalse(dbRecords.isEmpty());
            // Check if records sorted by ascending scores
            // 1. testPlayer2 - score:300
            // 2. testPlayer1 - score:200
            assertEquals(dbRecords.get().get(0).getPlayer(), testPlayer2);
            // Check only the recent week's records exist in the leaderboard.
            assertFalse(dbRecords.get().contains(testPlayer3));
        } catch (Exception e) {
            System.out.println("Exception occurred during Date parsing");
        }
    }

    @Test
    public void testMonthlyLeaderBoardRecords() {
        // Create player 1's leaderboard record
        Player testPlayer1 = new Player("test1", "test1123", "test1@t.com");
        Player savedPlayer1 = mPlayerRepository.save(testPlayer1);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, 2021);
        calendar1.set(Calendar.MONTH, 4);
        calendar1.set(Calendar.DATE, 1);
        Date date1 = calendar1.getTime();
        LeaderboardRecord testRecord1 = new LeaderboardRecord(savedPlayer1, date1, 200);
        LeaderboardRecord savedRecord1 = mLeaderboardRecordRepository.save(testRecord1);
        Optional<LeaderboardRecord> dbRecord1 = mLeaderboardRecordRepository.findById(savedRecord1.getId());
        // Create player 2's leaderboard record
        Player testPlayer2 = new Player("test2", "test2123", "test2@t.com");
        Player savedPlayer2 = mPlayerRepository.save(testPlayer2);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 2021);
        calendar2.set(Calendar.MONTH, 4);
        calendar2.set(Calendar.DATE, 9);
        Date date2 = calendar2.getTime();
        LeaderboardRecord testRecord2 = new LeaderboardRecord(savedPlayer2, date2, 300);
        LeaderboardRecord savedRecord2 = mLeaderboardRecordRepository.save(testRecord2);
        Optional<LeaderboardRecord> dbRecord2 = mLeaderboardRecordRepository.findById(savedRecord2.getId());
        // Create player 3's leaderboard record
        Player testPlayer3 = new Player("test3", "test3123", "test3@t.com");
        Player savedPlayer3 = mPlayerRepository.save(testPlayer3);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(Calendar.YEAR, 2021);
        calendar3.set(Calendar.MONTH, 3);
        calendar3.set(Calendar.DATE, 20);
        Date date3 = calendar2.getTime();
        LeaderboardRecord testRecord3 = new LeaderboardRecord(savedPlayer3, date3, 400);
        LeaderboardRecord savedRecord3 = mLeaderboardRecordRepository.save(testRecord3);
        Optional<LeaderboardRecord> dbRecord3 = mLeaderboardRecordRepository.findById(savedRecord3.getId());
        // Check db record was saved
        assertTrue(dbRecord1.isPresent());
        assertTrue(dbRecord2.isPresent());
        assertTrue(dbRecord3.isPresent());
        // Create the recent month's leaderboard records List
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date thirtyDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(30)));
        String thirtyDaysAgoDate = formatter.format(thirtyDaysAgo);
        Date now = new java.util.Date();
        String nowDate = formatter.format(now);
        try{
            Optional<List<LeaderboardRecord>> dbRecords = Optional.ofNullable(mLeaderboardRecordRepository.findByEndDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(nowDate),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(thirtyDaysAgoDate)));
            // Check db records saved
            assertTrue(dbRecords.isPresent());
            assertFalse(dbRecords.isEmpty());
            // Check only the recent month's records exist in the leaderboard.
            assertFalse(dbRecords.get().contains(testPlayer3));
        } catch (Exception e) {
            System.out.println("Exception occurred during Date parsing");
        }
    }

    @Test
    public void testAllTimesLeaderBoardRecords() {
        // Create player 1's leaderboard record
        Player testPlayer1 = new Player("test1", "test1123", "test1@t.com");
        Player savedPlayer1 = mPlayerRepository.save(testPlayer1);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, 2020);
        calendar1.set(Calendar.MONTH, 4);
        calendar1.set(Calendar.DATE, 1);
        Date date1 = calendar1.getTime();
        LeaderboardRecord testRecord1 = new LeaderboardRecord(savedPlayer1, date1, 200);
        LeaderboardRecord savedRecord1 = mLeaderboardRecordRepository.save(testRecord1);
        Optional<LeaderboardRecord> dbRecord1 = mLeaderboardRecordRepository.findById(savedRecord1.getId());
        // Create player 2's leaderboard record
        Player testPlayer2 = new Player("test2", "test2123", "test2@t.com");
        Player savedPlayer2 = mPlayerRepository.save(testPlayer2);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 2021);
        calendar2.set(Calendar.MONTH, 1);
        calendar2.set(Calendar.DATE, 9);
        Date date2 = calendar2.getTime();
        LeaderboardRecord testRecord2 = new LeaderboardRecord(savedPlayer2, date2, 300);
        LeaderboardRecord savedRecord2 = mLeaderboardRecordRepository.save(testRecord2);
        Optional<LeaderboardRecord> dbRecord2 = mLeaderboardRecordRepository.findById(savedRecord2.getId());
        // Create player 3's leaderboard record
        Player testPlayer3 = new Player("test3", "test3123", "test3@t.com");
        Player savedPlayer3 = mPlayerRepository.save(testPlayer3);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(Calendar.YEAR, 2021);
        calendar3.set(Calendar.MONTH, 4);
        calendar3.set(Calendar.DATE, 27);
        Date date3 = calendar2.getTime();
        LeaderboardRecord testRecord3 = new LeaderboardRecord(savedPlayer3, date3, 400);
        LeaderboardRecord savedRecord3 = mLeaderboardRecordRepository.save(testRecord3);
        Optional<LeaderboardRecord> dbRecord3 = mLeaderboardRecordRepository.findById(savedRecord3.getId());
        // Check db record was saved
        assertTrue(dbRecord1.isPresent());
        assertTrue(dbRecord2.isPresent());
        assertTrue(dbRecord3.isPresent());
        // Create the recent month's leaderboard records List
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date thirtyDaysAgo = Date.from(Instant.now().minus(Duration.ofDays(30)));
        String thirtyDaysAgoDate = formatter.format(thirtyDaysAgo);
        Date now = new java.util.Date();
        String nowDate = formatter.format(now);
        try{
            Optional<List<LeaderboardRecord>> dbRecords = Optional.ofNullable(mLeaderboardRecordRepository.findByEndDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(nowDate),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(thirtyDaysAgoDate)));
            // Check db records saved
            assertTrue(dbRecords.isPresent());
            assertFalse(dbRecords.isEmpty());
            // Check all times' users exist in the leaderboard
            assertEquals(dbRecords.get().get(0).getPlayer(), testPlayer3);
            assertEquals(dbRecords.get().get(1).getPlayer(), testPlayer2);
            assertEquals(dbRecords.get().get(2).getPlayer(), testPlayer1);
        } catch (Exception e) {
            System.out.println("Exception occurred during Date parsing");
        }
    }
}
