package com.wcd.playerevaluation.repository;

import com.wcd.playerevaluation.entity.Indexer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexerRepository extends JpaRepository<Indexer, Integer> {
}
