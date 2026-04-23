package com.wcd.playerevaluation.controller;

import com.wcd.playerevaluation.dto.request.PlayerRequestDTO;
import com.wcd.playerevaluation.dto.response.ApiResponseDTO;
import com.wcd.playerevaluation.dto.response.PlayerResponseDTO;
import com.wcd.playerevaluation.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    // =============================================
    // GET /api/players
    // Lấy danh sách toàn bộ người chơi
    // =============================================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PlayerResponseDTO>>> getAllPlayers() {
        List<PlayerResponseDTO> players = playerService.getAllPlayers();
        return ResponseEntity.ok(
                ApiResponseDTO.success("Lấy danh sách người chơi thành công", players)
        );
    }

    // =============================================
    // GET /api/players/{id}
    // Lấy thông tin chi tiết một người chơi
    // =============================================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PlayerResponseDTO>> getPlayerById(@PathVariable Integer id) {
        PlayerResponseDTO player = playerService.getPlayerById(id);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Lấy thông tin người chơi thành công", player)
        );
    }

    // =============================================
    // POST /api/players
    // Thêm người chơi mới
    // =============================================
    @PostMapping
    public ResponseEntity<ApiResponseDTO<PlayerResponseDTO>> createPlayer(
            @Valid @RequestBody PlayerRequestDTO dto) {
        PlayerResponseDTO created = playerService.createPlayer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDTO.success("Thêm người chơi mới thành công", created)
        );
    }

    // =============================================
    // PUT /api/players/{id}
    // Cập nhật thông tin người chơi
    // =============================================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PlayerResponseDTO>> updatePlayer(
            @PathVariable Integer id,
            @Valid @RequestBody PlayerRequestDTO dto) {
        PlayerResponseDTO updated = playerService.updatePlayer(id, dto);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Cập nhật thông tin người chơi thành công", updated)
        );
    }

    // =============================================
    // DELETE /api/players/{id}
    // Xóa người chơi
    // =============================================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deletePlayer(@PathVariable Integer id) {
        playerService.deletePlayer(id);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Xóa người chơi thành công", null)
        );
    }
}
