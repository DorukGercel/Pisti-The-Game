package com.group7.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "idgen", sequenceName = "GAME_SEQ")
@Entity
@Table(name="GAME")
public class Game extends BaseModel {
    @Column(name = "START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date start_time;

    @Column(name = "END_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date end_time;

    @Column(name = "END_SCORE")
    private int end_score;

    @ManyToOne(mappedBy = "played_games")
    Set<Player> players;

    @ManyToOne
    @JoinColumn(name = "ID", nullable = false)
    private LeaderBoard leaderboard;

}