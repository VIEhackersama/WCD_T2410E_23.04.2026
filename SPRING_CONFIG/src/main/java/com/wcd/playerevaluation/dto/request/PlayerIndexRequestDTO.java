package com.wcd.playerevaluation.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerIndexRequestDTO {

    @NotNull(message = "index_id của chỉ số không được để trống")
    @Positive(message = "index_id phải là số dương")
    private Integer indexId;

    @NotNull(message = "Giá trị (value) không được để trống")
    private Float value;
}
