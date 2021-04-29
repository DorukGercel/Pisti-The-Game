package com.group7.server.repository;

import com.group7.server.model.LeaderboardRecord;
import com.group7.server.model.Player;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class LeaderboardRecordRepositoryTestStub implements LeaderboardRecordRepository {


    @Override
    public List<Player> getAllPlayers() {
        return null;
    }

    @Override
    public List<LeaderboardRecord> findByEndDateBetween(Date startDate, Date endDate) {
        return null;
    }

    @Override
    public Optional<LeaderboardRecord> findByPlayer(Player player) {
        return Optional.empty();
    }

    @Override
    public List<LeaderboardRecord> findAll() {
        return null;
    }

    @Override
    public List<LeaderboardRecord> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<LeaderboardRecord> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<LeaderboardRecord> findAllById(Iterable<Long> iterable) {
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
    public void delete(LeaderboardRecord record) {

    }

    @Override
    public void deleteAll(Iterable<? extends LeaderboardRecord> iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends LeaderboardRecord> S save(S s) {
        return null;
    }

    @Override
    public <S extends LeaderboardRecord> List<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<LeaderboardRecord> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends LeaderboardRecord> S saveAndFlush(S s) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<LeaderboardRecord> iterable) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public LeaderboardRecord getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends LeaderboardRecord> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends LeaderboardRecord> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends LeaderboardRecord> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends LeaderboardRecord> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends LeaderboardRecord> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends LeaderboardRecord> boolean exists(Example<S> example) {
        return false;
    }
}
