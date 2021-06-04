package com.group7.server.definitions.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/** Used in leaderboard dto to indicate records.*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordEntry {
    private String playerName;
    private int    score;
    private Date   date;
}
