package com.group7.server.repository;

import com.group7.server.model.LeaderboardRecord;
import com.group7.server.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repository of the leaderboard records*/
@Repository
public interface LeaderboardRecordRepository
        extends JpaRepository<LeaderboardRecord, Long> {
    @Query("select p from Player p")
    List<Player> getAllPlayers();
}
