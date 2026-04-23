package com.wcd.playerevaluation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerIndexResponseDTO {

    private Integer id;
    private Integer indexId;
    private String indexName;
    private Float value;
    private Float valueMin;
    private Float valueMax;
}
