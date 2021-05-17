package com.group7.server.model;

import lombok.*;
import javax.persistence.*;
import java.util.Date;

/** Model of the leaderboard record created upon finish of a game*/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SequenceGenerator(name = "idgen", sequenceName = "LEADERBOARD_RECORD_SEQ")
@Entity
@Table(name="LEADERBOARD_RECORD")
public class LeaderboardRecord extends BaseModel {
    /** Reference to the player who played the game */
    @OneToOne
    @JoinColumn(name = "PLAYER_ID", referencedColumnName = "id", unique = true)
    private Player player;

    /** Date when the game is finished */
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date endDate;

    /** Overall score */
    @Column(name = "SCORE")
    private int score;

    public LeaderboardRecord(Player player, java.util.Date endDate, int score) {
        this.player = player;
        this.endDate = endDate;
        this.score = score;
    }
}