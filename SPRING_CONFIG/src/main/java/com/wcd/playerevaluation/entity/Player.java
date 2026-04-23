package com.wcd.playerevaluation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "player")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Integer playerId;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "full_name", nullable = false, length = 128)
    private String fullName;

    @Column(name = "age", nullable = false, length = 10)
    private String age;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "index_id", nullable = false)
    private Indexer indexer;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerIndex> playerIndexes;
}
