package com.group7.server.repository;

import com.group7.server.model.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardRepository
        extends JpaRepository<Leaderboard, Long> {}
