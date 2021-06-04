package com.group7.server.repository;

import com.group7.server.model.Player;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public class PlayerRepositoryTestStub implements PlayerRepository {

    @Override
    public Optional<Player> findById(long id) {
        Player testPlayer = new Player("Rabia", "lolFriends", "r@g.com");
        testPlayer.setId(1L);
        return Optional.of(testPlayer);
    }

    @Override
    public Optional<Player> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<Player> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void deleteByUsername(String username) {

    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public List<Player> findAll() {
        return null;
    }

    @Override
    public List<Player> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Player> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Player> findAllById(Iterable<Long> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Player player) {

    }

    @Override
    public void deleteAll(Iterable<? extends Player> iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Player> S save(S s) {
        return null;
    }

    @Override
    public <S extends Player> List<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<Player> findById(Long aLong) {
        Player testPlayer = new Player("Rabia", "lolFriends", "r@g.com");
        testPlayer.setId(aLong);
        return Optional.of(testPlayer);
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Player> S saveAndFlush(S s) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Player> iterable) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Player getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends Player> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Player> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Player> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Player> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Player> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Player> boolean exists(Example<S> example) {
        return false;
    }
}
