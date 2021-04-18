package com.group7.server.repository;

import com.group7.server.model.ActivePlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivePlayerRepository
        extends JpaRepository<ActivePlayer, Long> {}
