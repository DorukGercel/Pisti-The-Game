package com.group7.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ACTIVE_PLAYER")
public class ActivePlayer extends BaseModel {

    @OneToOne
    @JoinColumn(name = "PLAYER_ID", referencedColumnName = "id", unique = true)
    private Player player;

    @Column(name = "LEVEL")
    private int level;

    @Column(name = "SCORE")
    private int score;

    public ActivePlayer(Player player){
        this.player = player;
    }

}
