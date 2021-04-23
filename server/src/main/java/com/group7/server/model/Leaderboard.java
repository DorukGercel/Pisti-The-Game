package com.group7.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
//@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "idgen", sequenceName = "GAME_SEQ")
@Entity
@Table(name="LEADERBOARD")
public class Leaderboard extends BaseModel {
    // TODO Decide the relation
//    @OneToMany
//    private Set<Player> players;
}