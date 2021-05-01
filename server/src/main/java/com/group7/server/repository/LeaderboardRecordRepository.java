package com.group7.server.repository;

import com.group7.server.model.LeaderboardRecord;
import com.group7.server.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/** Repository of the leaderboard records*/
@Repository
public interface LeaderboardRecordRepository
        extends JpaRepository<LeaderboardRecord, Long>
{
    List<LeaderboardRecord> findAll();

    Optional<LeaderboardRecord> findByPlayerAndEndDate(Player player, Date date);

    @Query(value = "from LeaderboardRecord t where t.endDate between :startDate and :endDate order by t.score desc")
    List<LeaderboardRecord> findByEndDateBetween(
            @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("select lr from LeaderboardRecord lr, Player p where lr.player = p")
    Optional<LeaderboardRecord> findByPlayer(@Param("player") Player player);
}
