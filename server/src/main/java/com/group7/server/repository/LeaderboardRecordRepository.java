package com.group7.server.repository;

import com.group7.server.model.LeaderboardRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardRecordRepository
        extends JpaRepository<LeaderboardRecord, Long> {}
