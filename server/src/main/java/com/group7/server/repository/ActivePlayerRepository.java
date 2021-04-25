package com.group7.server.repository;

import com.group7.server.model.ActivePlayer;
import com.group7.server.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
/**Repository of the active (online) players*/
public interface ActivePlayerRepository
        extends JpaRepository<ActivePlayer, Long> {
    @Transactional
    void deleteById(long sessionId);
}
