package com.group7.server.repository;

import com.group7.server.model.LeaderBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderBoardRepository
        extends JpaRepository<LeaderBoard, Long> {}
