package com.group7.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ACTIVE_PLAYER")
/** Model of the player who is online (logged in) and includes session-wide information*/
public class ActivePlayer extends BaseModel {

    /** Reference to the owner player*/
    @NotNull
    @OneToOne
    @JoinColumn(name = "PLAYER_ID", referencedColumnName = "id", unique = true)
    private Player player;

    /** Current game level*/
    @Column(name = "LEVEL")
    private int level;

    /** Current overall score*/
    @Column(name = "SCORE")
    private int score;

    /** Constructor with the owner player argument*/
    public ActivePlayer(Player player){
        this.player = player;
        this.level = 1;
        this.score = 0;
    }

}
