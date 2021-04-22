package com.group7.server.repository;

import com.group7.server.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository
        extends JpaRepository<Game, Long> {
    Optional<Game> findGameById(long id);
}
