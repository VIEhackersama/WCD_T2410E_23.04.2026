package com.wcd.playerevaluation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "indexer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Indexer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_id")
    private Integer indexId;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "`valueMin`", nullable = false)
    private Float valueMin;

    @Column(name = "`valueMax`", nullable = false)
    private Float valueMax;

    @OneToMany(mappedBy = "indexer", cascade = CascadeType.ALL)
    private List<Player> players;

    @OneToMany(mappedBy = "indexer", cascade = CascadeType.ALL)
    private List<PlayerIndex> playerIndexes;
}
