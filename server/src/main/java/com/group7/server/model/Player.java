package com.group7.server.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "idgen", sequenceName = "PLAYER_SEQ")
@Entity
@Table(name="PLAYER")
public class Player extends BaseModel {
    @Column(name = "USERNAME", unique = true)
    private String username;

    @Column(name = "PASSWORD", unique = true)
    private String password;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @ManyToMany
    @JoinTable(
            name = "GAME",
            joinColumns = @JoinColumn(name = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ID"))
    Set<Game> played_games;
}
