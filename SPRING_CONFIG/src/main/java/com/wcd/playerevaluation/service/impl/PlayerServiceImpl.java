package com.wcd.playerevaluation.service.impl;

import com.wcd.playerevaluation.dto.request.PlayerIndexRequestDTO;
import com.wcd.playerevaluation.dto.request.PlayerRequestDTO;
import com.wcd.playerevaluation.dto.response.PlayerIndexResponseDTO;
import com.wcd.playerevaluation.dto.response.PlayerResponseDTO;
import com.wcd.playerevaluation.entity.Indexer;
import com.wcd.playerevaluation.entity.Player;
import com.wcd.playerevaluation.entity.PlayerIndex;
import com.wcd.playerevaluation.exception.ResourceNotFoundException;
import com.wcd.playerevaluation.exception.ValidationException;
import com.wcd.playerevaluation.repository.IndexerRepository;
import com.wcd.playerevaluation.repository.PlayerIndexRepository;
import com.wcd.playerevaluation.repository.PlayerRepository;
import com.wcd.playerevaluation.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final IndexerRepository indexerRepository;
    private final PlayerIndexRepository playerIndexRepository;

    // =============================================
    // GET ALL PLAYERS
    // =============================================
    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponseDTO> getAllPlayers() {
        return playerRepository.findAllByOrderByPlayerIdAsc()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // =============================================
    // GET PLAYER BY ID
    // =============================================
    @Override
    @Transactional(readOnly = true)
    public PlayerResponseDTO getPlayerById(Integer playerId) {
        Player player = findPlayerOrThrow(playerId);
        return mapToResponseDTO(player);
    }

    // =============================================
    // CREATE PLAYER
    // =============================================
    @Override
    @Transactional
    public PlayerResponseDTO createPlayer(PlayerRequestDTO dto) {
        // Validate tuổi là số nguyên dương
        validateAge(dto.getAge());

        // Kiểm tra tên người chơi không trùng
        if (playerRepository.existsByName(dto.getName())) {
            throw new ValidationException(
                    "Tên người chơi '" + dto.getName() + "' đã tồn tại trong hệ thống"
            );
        }

        // Lấy indexer chính
        Indexer indexer = findIndexerOrThrow(dto.getIndexId());

        // Tạo entity player
        Player player = new Player();
        player.setName(dto.getName());
        player.setFullName(dto.getFullName());
        player.setAge(dto.getAge());
        player.setIndexer(indexer);

        Player savedPlayer = playerRepository.save(player);

        // Lưu danh sách player_index nếu có
        if (dto.getPlayerIndexes() != null && !dto.getPlayerIndexes().isEmpty()) {
            List<PlayerIndex> indexes = buildPlayerIndexes(dto.getPlayerIndexes(), savedPlayer);
            playerIndexRepository.saveAll(indexes);
            savedPlayer.setPlayerIndexes(indexes);
        }

        return mapToResponseDTO(savedPlayer);
    }

    // =============================================
    // UPDATE PLAYER
    // =============================================
    @Override
    @Transactional
    public PlayerResponseDTO updatePlayer(Integer playerId, PlayerRequestDTO dto) {
        Player player = findPlayerOrThrow(playerId);

        // Validate tuổi là số nguyên dương
        validateAge(dto.getAge());

        // Kiểm tra tên không trùng với người chơi khác
        if (playerRepository.existsByNameAndPlayerIdNot(dto.getName(), playerId)) {
            throw new ValidationException(
                    "Tên người chơi '" + dto.getName() + "' đã được sử dụng bởi người chơi khác"
            );
        }

        // Lấy indexer chính
        Indexer indexer = findIndexerOrThrow(dto.getIndexId());

        // Cập nhật thông tin player
        player.setName(dto.getName());
        player.setFullName(dto.getFullName());
        player.setAge(dto.getAge());
        player.setIndexer(indexer);

        // Cập nhật player_indexes: xóa cũ, thêm mới
        playerIndexRepository.deleteByPlayer_PlayerId(playerId);

        if (dto.getPlayerIndexes() != null && !dto.getPlayerIndexes().isEmpty()) {
            List<PlayerIndex> newIndexes = buildPlayerIndexes(dto.getPlayerIndexes(), player);
            playerIndexRepository.saveAll(newIndexes);
            player.setPlayerIndexes(newIndexes);
        } else {
            player.setPlayerIndexes(new ArrayList<>());
        }

        Player updatedPlayer = playerRepository.save(player);
        return mapToResponseDTO(updatedPlayer);
    }

    // =============================================
    // DELETE PLAYER
    // =============================================
    @Override
    @Transactional
    public void deletePlayer(Integer playerId) {
        Player player = findPlayerOrThrow(playerId);
        // Xóa player_indexes liên quan trước
        playerIndexRepository.deleteByPlayer_PlayerId(playerId);
        playerRepository.delete(player);
    }

    // =============================================
    // PRIVATE HELPERS
    // =============================================

    private Player findPlayerOrThrow(Integer playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player", "player_id", playerId));
    }

    private Indexer findIndexerOrThrow(Integer indexId) {
        return indexerRepository.findById(indexId)
                .orElseThrow(() -> new ResourceNotFoundException("Indexer", "index_id", indexId));
    }

    private void validateAge(String age) {
        try {
            int ageVal = Integer.parseInt(age);
            if (ageVal <= 0 || ageVal > 100) {
                throw new ValidationException("Tuổi phải là số nguyên dương từ 1 đến 100");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Tuổi phải là số nguyên hợp lệ");
        }
    }

    private List<PlayerIndex> buildPlayerIndexes(
            List<PlayerIndexRequestDTO> indexDTOs, Player player) {

        List<PlayerIndex> result = new ArrayList<>();
        for (PlayerIndexRequestDTO indexDTO : indexDTOs) {
            Indexer indexer = findIndexerOrThrow(indexDTO.getIndexId());

            // Validate value nằm trong khoảng [valueMin, valueMax]
            float value = indexDTO.getValue();
            if (value < indexer.getValueMin() || value > indexer.getValueMax()) {
                throw new ValidationException(
                        String.format("Giá trị '%s' của chỉ số '%s' phải nằm trong khoảng [%.2f, %.2f]",
                                value, indexer.getName(), indexer.getValueMin(), indexer.getValueMax())
                );
            }

            PlayerIndex playerIndex = new PlayerIndex();
            playerIndex.setPlayer(player);
            playerIndex.setIndexer(indexer);
            playerIndex.setValue(value);
            result.add(playerIndex);
        }
        return result;
    }

    private PlayerResponseDTO mapToResponseDTO(Player player) {
        // Map indexer chính
        PlayerResponseDTO.IndexerDTO indexerDTO = PlayerResponseDTO.IndexerDTO.builder()
                .indexId(player.getIndexer().getIndexId())
                .name(player.getIndexer().getName())
                .valueMin(player.getIndexer().getValueMin())
                .valueMax(player.getIndexer().getValueMax())
                .build();

        // Map danh sách player_indexes từ DB (tránh lazy load)
        List<PlayerIndex> rawIndexes = playerIndexRepository.findByPlayer_PlayerId(player.getPlayerId());
        List<PlayerIndexResponseDTO> indexDTOs = rawIndexes.stream()
                .map(pi -> PlayerIndexResponseDTO.builder()
                        .id(pi.getId())
                        .indexId(pi.getIndexer().getIndexId())
                        .indexName(pi.getIndexer().getName())
                        .value(pi.getValue())
                        .valueMin(pi.getIndexer().getValueMin())
                        .valueMax(pi.getIndexer().getValueMax())
                        .build())
                .collect(Collectors.toList());

        return PlayerResponseDTO.builder()
                .playerId(player.getPlayerId())
                .name(player.getName())
                .fullName(player.getFullName())
                .age(player.getAge())
                .indexer(indexerDTO)
                .playerIndexes(indexDTOs)
                .build();
    }
}
