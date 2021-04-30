package com.group7.server.api;


import com.group7.server.definitions.common.StatusCode;
import com.group7.server.definitions.game.Game;
import com.group7.server.dto.leaderboard.LeaderboardResponse;
import com.group7.server.dto.leaderboard.ListRecordsResponse;
import com.group7.server.model.LeaderboardRecord;
import com.group7.server.service.leaderboard.LeaderboardRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
@Api(value = "Leaderboard Record API", tags = {"Leaderboard Record API"})
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
    @ApiOperation(value = "Creates a new record in the leaderboard. Login required.")
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
    @ApiOperation(value = "Updates a record in the leaderboard. Login required.")
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
    @ApiOperation(value = "Deletes a record from the leaderboard. Login required.")
    public LeaderboardResponse deleteRecord(@RequestBody LeaderboardRecord record) {
        StatusCode statusCode = mLeaderboardRecordService.deleteRecord(record);
        if (statusCode.equals(StatusCode.SUCCESS)) {
            return new LeaderboardResponse(statusCode, null);
        }
        return new LeaderboardResponse(statusCode, "Delete record operation failed!");
    }

    /**
     * Handles getting of all types of records in the leaderboard: all times, weekly, monthly.
     * Utilizes LeaderboardRecordService's method to deal with the request.
     *
     * @return  the leaderboard record response according to the success of the operation.
     *                      If operation is successful; returns success status code and game id
     *                                                ; error message is null.
     *                      If operation is not successful; returns fail status code and the error message.
     */
    @GetMapping("/get/{period}")
    @ApiOperation(value = "Lists all the records in the leaderboard. Login required.")
    public LeaderboardResponse getRecords(@PathVariable String period) {
        List<LeaderboardRecord> leaderboardRecordList = new ArrayList<>();
        StatusCode statusCode = mLeaderboardRecordService.getRecordsByDate(decodePeriodType(period), leaderboardRecordList);
        if(statusCode.equals(StatusCode.SUCCESS)) {
            return new ListRecordsResponse(StatusCode.SUCCESS, null, leaderboardRecordList);
        }
        return new LeaderboardResponse(statusCode, "Get records operation failed!");
    }


    /** Helper function to decode string to enum*/
    private LeaderboardRecordService.Period decodePeriodType(String moveTypeStr){
        if(moveTypeStr.equals("ALL TIMES")){
            return LeaderboardRecordService.Period.ALL_TIMES;
        }
        else if(moveTypeStr.equals("WEEKLY")){
            return LeaderboardRecordService.Period.WEEKLY;
        }
        else if(moveTypeStr.equals("MONTHLY")){
            return LeaderboardRecordService.Period.MONTHLY;
        }
        return null;
    }
}
