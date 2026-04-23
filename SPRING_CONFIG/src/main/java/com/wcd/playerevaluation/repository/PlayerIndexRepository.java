package com.wcd.playerevaluation.repository;

import com.wcd.playerevaluation.entity.PlayerIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerIndexRepository extends JpaRepository<PlayerIndex, Integer> {
    List<PlayerIndex> findByPlayer_PlayerId(Integer playerId);
    void deleteByPlayer_PlayerId(Integer playerId);
}
