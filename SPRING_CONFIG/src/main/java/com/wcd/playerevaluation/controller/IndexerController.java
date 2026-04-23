package com.wcd.playerevaluation.controller;

import com.wcd.playerevaluation.dto.response.ApiResponseDTO;
import com.wcd.playerevaluation.entity.Indexer;
import com.wcd.playerevaluation.exception.ResourceNotFoundException;
import com.wcd.playerevaluation.repository.IndexerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/indexers")
@RequiredArgsConstructor
public class IndexerController {

    private final IndexerRepository indexerRepository;

    // GET /api/indexers — Lấy toàn bộ danh sách chỉ số (index)
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Indexer>>> getAllIndexers() {
        List<Indexer> indexers = indexerRepository.findAll();
        // Bỏ circular reference trước khi trả về
        indexers.forEach(i -> { i.setPlayers(null); i.setPlayerIndexes(null); });
        return ResponseEntity.ok(
                ApiResponseDTO.success("Get list successfully", indexers)
        );
    }

    // GET /api/indexers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Indexer>> getIndexerById(@PathVariable Integer id) {
        Indexer indexer = indexerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Indexer", "index_id", id));
        indexer.setPlayers(null);
        indexer.setPlayerIndexes(null);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Get indexes successfully", indexer)
        );
    }
}
