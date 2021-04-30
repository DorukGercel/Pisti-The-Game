package com.group7.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/** Model of the player who is online (logged in) and includes session-wide information*/
@Data
@Entity
@Table(name = "ACTIVE_PLAYER")
public class ActivePlayer extends BaseModel {

    /** Reference to the owner player*/
    @NotNull
    @OneToOne
    @JoinColumn(name = "PLAYER_ID", referencedColumnName = "id", unique = true)
    private Player player;

    /** Current game level*/
    @NotNull
    @Column(name = "LEVEL")
    private int level;

    /** Current overall score*/
    @NotNull
    @Column(name = "SCORE")
    private int score;

    /** Currently attached game*/
    @NotNull
    @Column(name = "GAME_ID")
    private long gameId;

    public ActivePlayer(){
        this.player = null;
        this.level = 0;
        this.score = 0;
        this.gameId = -1L;
    }

    /** Constructor with the owner player argument*/
    public ActivePlayer(Player player){
        this.player = player;
        this.level = 0;
        this.score = 0;
        this.gameId = -1L;
    }
}
