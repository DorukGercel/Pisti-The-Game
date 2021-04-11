package com.group7.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

//TODO: Implement fields of the player model
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="PLAYER")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;
}
