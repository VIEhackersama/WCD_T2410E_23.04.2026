package com.wcd.playerevaluation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponseDTO {

    private Integer playerId;
    private String name;
    private String fullName;
    private String age;
    private IndexerDTO indexer;
    private List<PlayerIndexResponseDTO> playerIndexes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndexerDTO {
        private Integer indexId;
        private String name;
        private Float valueMin;
        private Float valueMax;
    }
}
