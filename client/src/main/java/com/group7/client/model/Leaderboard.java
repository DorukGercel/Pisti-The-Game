package com.group7.client.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;

@Data
public class Leaderboard {
    private SimpleIntegerProperty rank;
    private SimpleStringProperty  username;
    private SimpleIntegerProperty score;

    public Leaderboard(Integer rank, String username, Integer score) {
        this.rank = new SimpleIntegerProperty(rank);
        this.username = new SimpleStringProperty(username);
        this.score = new SimpleIntegerProperty(score);
    }

    public Integer getRank() {
        return this.rank.get();
    }

    public String getUsername() {
        return this.username.get();
    }

    public Integer getScore() {
        return this.score.get();
    }

    public void setRank(int rank) {
        this.rank.set(rank);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setScore(int score) {
        this.score.set(score);
    }
}
