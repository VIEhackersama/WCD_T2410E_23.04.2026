package com.wcd.playerevaluation.repository;

import com.wcd.playerevaluation.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    boolean existsByName(String name);
    boolean existsByNameAndPlayerIdNot(String name, Integer playerId);
    List<Player> findAllByOrderByPlayerIdAsc();
}
