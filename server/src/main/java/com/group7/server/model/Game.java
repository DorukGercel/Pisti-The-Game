package com.group7.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

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

    @OneToMany(mappedBy = "game")
    Set<ActivePlayer> active_players;
}