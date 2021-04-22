package com.group7.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "idgen", sequenceName = "GAME_SEQ")
@Entity
@Table(name="LEADERBOARD")
public class LeaderBoard extends BaseModel {
    @OneToMany(mappedBy = "leaderboard")
    private Set<Game> games;
}