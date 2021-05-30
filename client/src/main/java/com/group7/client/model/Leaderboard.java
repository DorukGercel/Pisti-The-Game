package com.group7.client.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Leaderboard {
    private SimpleIntegerProperty rank;
    private SimpleStringProperty  username;
    private SimpleIntegerProperty score;

    public Leaderboard(Integer rank, String username, Integer score) {
        this.rank = new SimpleIntegerProperty(rank);
        this.username = new SimpleStringProperty(username);
        this.score = new SimpleIntegerProperty(score);
    }

    public Leaderboard(String username, String endDate, Integer rank, Integer score) {
        getRank(rank);
        getUsername(username);
        getScore(score);
    }

    public Integer getRank(Integer rank) {
        return this.rank.get();
    }

    public String getUsername(String username) {
        return this.username.get();
    }

    public Integer getScore(Integer score) {
        return this.score.get();
    }

}
