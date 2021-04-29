package com.group7.server.repository;

import com.group7.server.model.LeaderboardRecord;
import com.group7.server.model.Player;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

public class LeaderboardRecordRepositoryTestStub implements LeaderboardRecordRepository {

    static List<LeaderboardRecord> recordList = new ArrayList<>();

    @Override
    public List<LeaderboardRecord> findByEndDateBetween(Date startDate, Date endDate) {
        List<LeaderboardRecord> tmpList = new ArrayList<>();
        for(LeaderboardRecord record : recordList) {
            if (record.getEndDate().after(endDate) && record.getEndDate().before(startDate)) {
                tmpList.add(record);
            }
        }
        Collections.sort(tmpList, new Comparator<>() {
            @Override
            public int compare(LeaderboardRecord o1, LeaderboardRecord o2) {
                if (o1.getScore() == o2.getScore())
                    return 0;
                else if (o1.getScore() > o2.getScore())
                    return -1;
                else
                    return 1;
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });

        return tmpList;
    }


    @Override
    public Optional<LeaderboardRecord> findByPlayer(Player player) {
        return Optional.empty();
    }

    @Override
    public List<LeaderboardRecord> findAll() {
        Collections.sort(recordList, new Comparator<>() {
            @Override
            public int compare(LeaderboardRecord o1, LeaderboardRecord o2) {
                if (o1.getScore() == o2.getScore())
                    return 0;
                else if (o1.getScore() > o2.getScore())
                    return -1;
                else
                    return 1;
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });
        return recordList;
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
        recordList.remove(0);
    }

    @Override
    public void delete(LeaderboardRecord record) {

    }

    @Override
    public void deleteAll(Iterable<? extends LeaderboardRecord> iterable) {

    }

    @Override
    public void deleteAll() {
        recordList.clear();
    }

    @Override
    public <S extends LeaderboardRecord> S save(S s) {
        recordList.add(s);
        return s;
    }

    @Override
    public <S extends LeaderboardRecord> List<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<LeaderboardRecord> findById(Long aLong) {
        if (recordList.isEmpty()){
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(recordList.get(0));
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
