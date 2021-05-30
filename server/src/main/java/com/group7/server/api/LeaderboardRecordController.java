package com.group7.server.api;


import com.group7.server.definitions.common.StatusCode;
import com.group7.server.definitions.leaderboard.RecordEntry;
import com.group7.server.dto.leaderboard.LeaderboardRequest;
import com.group7.server.dto.leaderboard.LeaderboardResponse;
import com.group7.server.dto.leaderboard.ListRecordsResponse;
import com.group7.server.service.leaderboard.LeaderboardRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for leaderboard related requests of the players.
 * Deals with the display of leaderboard records for the most recent week,
 * the most recent month, and for all times.
 *
 */
@RequiredArgsConstructor
@RequestMapping("/api/leaderboard")
@Api(value = "Leaderboard RecordEntry API", tags = {"Leaderboard RecordEntry API"})
@RestController
public class LeaderboardRecordController {

    private final LeaderboardRecordService mLeaderboardRecordService;

    /**
     * Handles creation of a new record in the leaderboard.
     * Utilizes LeaderboardRecordService's method to deal with the request.
     *
     * @param recordRequest contains the record which belongs to a player who just finished his/her first game.
     * @return the leaderboard record response according to the success of the operation.
     *                      If operation is successful; returns success status code.
     *                                                ; error message is null.
     *                      If operation is not successful; returns fail status code and the error message.
     */
    @PostMapping("/create")
    @ApiOperation(value = "Creates a new record in the leaderboard. Login required.")
    public LeaderboardResponse createRecord(@RequestBody LeaderboardRequest recordRequest) {
        StatusCode statusCode = mLeaderboardRecordService.createRecord(recordRequest.getPlayerId(), recordRequest.getDate(), recordRequest.getScore());
        if(statusCode.equals(StatusCode.SUCCESS)) {
            return new LeaderboardResponse(statusCode, null);
        }
        return new LeaderboardResponse(statusCode, "Create record operation failed!");
    }

    /**
     * Handles update of an existing record in the leaderboard.
     * Utilizes LeaderboardRecordService's method to deal with the request.
     *
     * @param recordRequest contains the record to be updated.
     * @return the leaderboard record response according to the success of the operation.
     *                      If operation is successful; returns success status code.
     *                                                ; error message is null.
     *                    If operation is not successful; returns fail status code and the error message.
     */
    @PostMapping("/update")
    @ApiOperation(value = "Updates a record in the leaderboard. Login required.")
    public LeaderboardResponse updateRecord(@RequestBody LeaderboardRequest recordRequest) {
        StatusCode statusCode = mLeaderboardRecordService.updateRecord(recordRequest.getRecordId() ,recordRequest.getPlayerId(), recordRequest.getDate(), recordRequest.getScore());
        if(statusCode.equals(StatusCode.SUCCESS)) {
            return new LeaderboardResponse(statusCode, null);
        }
        return new LeaderboardResponse(statusCode, "Update record operation failed!");
    }

    /**
     * Handles deletion of an existing record in the leaderboard.
     * Utilizes LeaderboardRecordService's method to deal with the request.
     *
     * @param recordRequest contains the record to be deleted.
     * @return the leaderboard record response according to the success of the operation.
     *                      If operation is successful; returns success status code.
     *                                                ; error message is null.
     *                      If operation is not successful; returns fail status code and the error message.
     *
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "Deletes a record from the leaderboard. Login required.")
    public LeaderboardResponse deleteRecord(@RequestBody LeaderboardRequest recordRequest) {
        StatusCode statusCode = mLeaderboardRecordService.deleteRecord(recordRequest.getRecordId());
        if (statusCode.equals(StatusCode.SUCCESS)) {
            return new LeaderboardResponse(statusCode, null);
        }
        return new LeaderboardResponse(statusCode, "Delete record operation failed!");
    }

    /**
     * Handles getting of all types of records in the leaderboard: all times, weekly, monthly.
     * Utilizes LeaderboardRecordService's method to deal with the request.
     *
     * @param period to be used in filtering leaderboard records.
     * @return  the leaderboard record response according to the success of the operation.
     *                      If operation is successful; returns success status code and list of records
     *                                                ; error message is null.
     *                      If operation is not successful; returns fail status code and the error message.
     */
    @GetMapping("/get/{period}")
    @ApiOperation(value = "Lists the records in the leaderboard by period. Login required.")
    public LeaderboardResponse getRecords(@PathVariable String period) {
        List<RecordEntry> recordEntryList = new ArrayList<>();
        StatusCode statusCode = mLeaderboardRecordService.getRecordsByDate(decodePeriodType(period), recordEntryList);
        if(statusCode.equals(StatusCode.SUCCESS)) {
            // TODO: Remove print
            System.out.println(recordEntryList);
            System.out.println(new ListRecordsResponse(StatusCode.SUCCESS, null, recordEntryList));
            return new ListRecordsResponse(StatusCode.SUCCESS, null, recordEntryList);
        }
        return new LeaderboardResponse(statusCode, "Get records operation failed!");
    }


    /** Helper function to decode string to enum*/
    private LeaderboardRecordService.Period decodePeriodType(String periodTypeStr){
        if(periodTypeStr.equals("allTimes")){
            return LeaderboardRecordService.Period.ALL_TIMES;
        }
        else if(periodTypeStr.equals("weekly")){
            return LeaderboardRecordService.Period.WEEKLY;
        }
        else if(periodTypeStr.equals("monthly")){
            return LeaderboardRecordService.Period.MONTHLY;
        }
        return null;
    }
}
