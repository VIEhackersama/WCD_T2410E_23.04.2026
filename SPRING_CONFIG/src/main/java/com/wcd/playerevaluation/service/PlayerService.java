package com.wcd.playerevaluation.service;

import com.wcd.playerevaluation.dto.request.PlayerRequestDTO;
import com.wcd.playerevaluation.dto.response.PlayerResponseDTO;

import java.util.List;

public interface PlayerService {

    /**
     * Lấy danh sách tất cả người chơi
     */
    List<PlayerResponseDTO> getAllPlayers();

    /**
     * Lấy thông tin người chơi theo ID
     */
    PlayerResponseDTO getPlayerById(Integer playerId);

    /**
     * Thêm người chơi mới
     */
    PlayerResponseDTO createPlayer(PlayerRequestDTO dto);

    /**
     * Cập nhật thông tin người chơi
     */
    PlayerResponseDTO updatePlayer(Integer playerId, PlayerRequestDTO dto);

    /**
     * Xóa người chơi theo ID
     */
    void deletePlayer(Integer playerId);
}
