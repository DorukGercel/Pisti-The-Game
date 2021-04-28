package com.group7.server.repository;

import com.group7.server.model.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlayerRepositoryTest {

    private PlayerRepository mPlayerRepository;

    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository){
        this.mPlayerRepository = playerRepository;
    }

    @Test
    public void testCreatePlayer() {
        // Create a new Player
        Player testPlayer = new Player("test", "test", "test@test.com");
        Player savedPlayer = mPlayerRepository.save(testPlayer);
        assertNotNull(savedPlayer);
    }

    @Test
    public void testReadPlayer() {
        // Create a new Player
        Player testPlayer = new Player("test", "test", "test@test.com");
        Player savedPlayer = mPlayerRepository.save(testPlayer);
        // Read by ID
        Optional<Player> dbPlayer = mPlayerRepository.findById(savedPlayer.getId());
        assertFalse(dbPlayer.isEmpty());
        // Read by Username
        dbPlayer = mPlayerRepository.findByUsername(savedPlayer.getUsername());
        assertFalse(dbPlayer.isEmpty());
        // Read by Email
        dbPlayer = mPlayerRepository.findByEmail(savedPlayer.getEmail());
        assertFalse(dbPlayer.isEmpty());
    }

    @Test
    public void testUpdatePlayer() {
        // Create a player and save
        Player testPlayer = new Player("test", "test", "test@test.com");
        Player savedPlayer = mPlayerRepository.save(testPlayer);
        Optional<Player> dbPlayer = mPlayerRepository.findById(savedPlayer.getId());
        // Check db player was saved
        assertTrue(dbPlayer.isPresent());
        // Update username
        dbPlayer.get().setUsername("momo");
        dbPlayer.get().setPassword(dbPlayer.get().getPassword());
        dbPlayer.get().setEmail(dbPlayer.get().getEmail());
        Player updatedPlayer = mPlayerRepository.save(dbPlayer.get());
        // Check name changed other parts unchanged
        assertEquals("momo", updatedPlayer.getUsername());
        assertEquals("test", updatedPlayer.getPassword());
        assertEquals("test@test.com", updatedPlayer.getEmail());
    }

    @Test
    public void testDeletePlayer() {
        // Create players and save
        Player testPlayer1 = new Player("test", "test", "test@test.com");
        Player testPlayer2 = new Player("momo", "momo", "momo");
        Player savedPlayer1 = mPlayerRepository.save(testPlayer1);
        Player savedPlayer2 = mPlayerRepository.save(testPlayer2);
        Optional<Player> dbPlayer1 = mPlayerRepository.findById(savedPlayer1.getId());
        Optional<Player> dbPlayer2 = mPlayerRepository.findById(savedPlayer2.getId());
        // Check db players were saved
        assertTrue(dbPlayer1.isPresent());
        assertTrue(dbPlayer2.isPresent());

        // Delete  by id
        mPlayerRepository.deleteById(dbPlayer1.get().getId());
        Optional<Player> deletedPlayer1 = mPlayerRepository.findById(savedPlayer1.getId());
        // Delete by username
        mPlayerRepository.deleteByUsername(dbPlayer2.get().getUsername());
        Optional<Player> deletedPlayer2 = mPlayerRepository.findById(savedPlayer2.getId());
        // Check deletion
        assertTrue(deletedPlayer1.isEmpty());
        assertTrue(deletedPlayer2.isEmpty());

    }
}
