package com.group7.server.repository;

import com.group7.server.model.ActivePlayer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ActivePlayerRepositoryTest {

    private ActivePlayerRepository mActivePlayerRepository;

    @Autowired
    public void setActivePlayerRepository(ActivePlayerRepository activePlayerRepository){
        this.mActivePlayerRepository = activePlayerRepository;
    }

    @Test
    public void testCreateActivePlayer() {
        // Create a new Active Player
        ActivePlayer testActivePlayer = new ActivePlayer();
        ActivePlayer savedActivePlayer = mActivePlayerRepository.save(testActivePlayer);
        assertNotNull(savedActivePlayer);
    }

    @Test
    public void testReadPlayer() {
        // Create a new Active Player
        ActivePlayer testActivePlayer = new ActivePlayer();
        ActivePlayer savedActivePlayer = mActivePlayerRepository.save(testActivePlayer);
        // Read by ID
        Optional<ActivePlayer> dbActivePlayer = mActivePlayerRepository.findById(savedActivePlayer.getId());
        assertFalse(dbActivePlayer.isEmpty());
    }

    @Test
    public void testUpdatePlayer() {
        // Create a player and save
        ActivePlayer testActivePlayer = new ActivePlayer();
        ActivePlayer savedActivePlayer = mActivePlayerRepository.save(testActivePlayer);
        Optional<ActivePlayer> dbActivePlayer = mActivePlayerRepository.findById(savedActivePlayer.getId());
        // Check db player was saved
        assertTrue(dbActivePlayer.isPresent());
        // Update username
        dbActivePlayer.get().setId(dbActivePlayer.get().getId());
        dbActivePlayer.get().setScore((short) 100);
        dbActivePlayer.get().setLevel((short) 3);
        dbActivePlayer.get().setGameId(dbActivePlayer.get().getGameId());
        dbActivePlayer.get().setPlayer(dbActivePlayer.get().getPlayer());
        ActivePlayer updatedActivePlayer = mActivePlayerRepository.save(dbActivePlayer.get());
        // Check name changed other parts unchanged
        assertEquals(savedActivePlayer.getId(), updatedActivePlayer.getId());
        assertEquals(100, updatedActivePlayer.getScore());
        assertEquals(3, updatedActivePlayer.getLevel());
        assertEquals(savedActivePlayer.getGameId(), updatedActivePlayer.getGameId());
        assertEquals(savedActivePlayer.getPlayer(), updatedActivePlayer.getPlayer());

    }

    @Test
    public void testDeletePlayer() {
        // Create players and save
        ActivePlayer testActivePlayer = new ActivePlayer();
        ActivePlayer savedActivePlayer = mActivePlayerRepository.save(testActivePlayer);
        Optional<ActivePlayer> dbActivePlayer = mActivePlayerRepository.findById(savedActivePlayer.getId());
        // Check db players were saved
        assertTrue(dbActivePlayer.isPresent());

        // Delete  by id
        mActivePlayerRepository.deleteById(dbActivePlayer.get().getId());
        Optional<ActivePlayer> deletedActivePlayer = mActivePlayerRepository.findById(savedActivePlayer.getId());
        // Check deletion
        assertTrue(deletedActivePlayer.isEmpty());
    }
}

