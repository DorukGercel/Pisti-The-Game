package com.group7.server.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ACTIVE_PLAYER")
public class ActivePlayer {
    @Id
    @GeneratedValue(generator = "ForeignGenerator")
    @GenericGenerator(
            name = "ForeignGenerator",
            strategy = "foreign",
            parameters = @Parameter(name = "property", value = "player"))
    private Long id;

    @Column(name = "LEVEL")
    private int level;

    @Column(name = "SCORE")
    private int score;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public ActivePlayer(Player player){
        this.player = player;
    }

}
