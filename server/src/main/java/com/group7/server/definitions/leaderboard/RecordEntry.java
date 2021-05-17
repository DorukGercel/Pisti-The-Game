package com.group7.server.definitions.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class RecordEntry {
    private String playerName;
    private int    score;
    private Date   date;
}
