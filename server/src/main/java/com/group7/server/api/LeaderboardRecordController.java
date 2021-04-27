package com.group7.server.api;


import com.group7.server.model.LeaderboardRecord;
import com.group7.server.service.leaderboard.LeaderboardRecordService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * Responsible for leaderboard related requests of the players.
 * Deals with the display of leaderboard records for the most recent week,
 * the most recent month, and for all times.
 *
 */
@RequiredArgsConstructor
@RequestMapping("/api/leaderboard")
@RestController
public class LeaderboardRecordController {

    private final LeaderboardRecordService mLeaderboardRecordService;

    /**
     * Handles creation of a new record in the leaderboard.
     * Utilizes LeaderboardRecordService's method to deal with the request.
     *
     * @param record the record which belongs to a player who just finished his/her first game.
     * @return ResponseEntity with created LeaderboardRecord and HTTP 200 response code.
     *
     */
    @PostMapping("/create")
    public ResponseEntity<LeaderboardRecord> createRecord(@RequestBody LeaderboardRecord record) {
        return ResponseEntity.ok().body(mLeaderboardRecordService.createRecord(record));
    }

    /**
     * Handles update of an existing record in the leaderboard.
     * Utilizes LeaderboardRecordService's method to deal with the request.
     *
     * @param record the record which belongs to a player who just finished his/her first game.
     * @return ResponseEntity with updated LeaderboardRecord and HTTP 200 OK response code.
     *
     */
    @PostMapping("/update")
    public ResponseEntity<LeaderboardRecord> updateRecord(@RequestBody LeaderboardRecord record) {
        return ResponseEntity.ok().body(mLeaderboardRecordService.updateRecord(record));
    }

    /**
     * Handles deletion of an existing record in the leaderboard.
     * Utilizes LeaderboardRecordService's method to deal with the request.
     *
     * @param record the record which belongs to a player who just finished his/her first game.
     * @return ResponseEntity with HTTP 204 no content response code.
     *
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteRecord(@RequestBody LeaderboardRecord record) {
        mLeaderboardRecordService.deleteRecord(record);
        return ResponseEntity.noContent().build();
    }

    /**
     * Handles getting of all records in the leaderboard.
     * Utilizes LeaderboardRecordService's method to deal with the request.
     *
     * @return ResponseEntity with list of all leaderboard records and HTTP 200 OK response code.
     *
     */
    @GetMapping("/allTimes")
    public ResponseEntity<List<LeaderboardRecord>> getAllTimes(@RequestParam(value = "allTimes") String period) {
        return ResponseEntity.ok().body(mLeaderboardRecordService.getRecordsByDate("allTimes"));
    }

    /**
     * Handles getting of the last 7 days' records in the leaderboard.
     * Utilizes LeaderboardRecordService's method to deal with the request.
     *
     * @return ResponseEntity with list of the last 7 days' leaderboard records and HTTP 200 OK response code.
     *
     */
    @GetMapping("/weekly")
    public ResponseEntity<List<LeaderboardRecord>> getWeekly(@RequestParam(value = "weekly") String period) {
        return ResponseEntity.ok().body(mLeaderboardRecordService.getRecordsByDate("weekly"));
    }

    /**
     * Handles getting of the last 30 days' records in the leaderboard.
     * Utilizes LeaderboardRecordService's method to deal with the request.
     *
     * @return ResponseEntity with list of the last 30 days' leaderboard records and HTTP 200 OK response code.
     *
     */
    @GetMapping("/monthly")
    public ResponseEntity<List<LeaderboardRecord>> getMonthly(@RequestParam(value = "monthly") String period) {
        return ResponseEntity.ok().body(mLeaderboardRecordService.getRecordsByDate("monthly"));
    }
}
