package com.wcd.playerevaluation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player_index")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "index_id", nullable = false)
    private Indexer indexer;

    @Column(name = "value", nullable = false)
    private Float value;
}
