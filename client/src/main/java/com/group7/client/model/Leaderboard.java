package com.group7.client.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Leaderboard {
    private final SimpleStringProperty  username = new SimpleStringProperty("");
    private final SimpleStringProperty  endDate  = new SimpleStringProperty("");
    private final SimpleIntegerProperty score    = new SimpleIntegerProperty();
    private final SimpleIntegerProperty rank     = new SimpleIntegerProperty();

    public Leaderboard() {
        this("", "", -1, -1);
    }

    public Leaderboard(String username, String endDate, Integer rank, Integer score) {
        getUsername(username);
        getEndDate(endDate);
        getRank(rank);
        getScore(score);
    }

    public String getUsername(String username) {
        return this.username.get();
    }

    public String getEndDate(String endDate) {
        return this.endDate.get();
    }

    public Integer getScore(Integer score) {
        return this.score.get();
    }

    public Integer getRank(Integer rank) {
        return this.rank.get();
    }
}
