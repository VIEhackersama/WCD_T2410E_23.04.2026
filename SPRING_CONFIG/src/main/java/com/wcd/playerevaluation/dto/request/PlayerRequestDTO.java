package com.wcd.playerevaluation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRequestDTO {

    @NotBlank(message = "Tên (name) không được để trống")
    @Size(max = 64, message = "Tên (name) không được vượt quá 64 ký tự")
    private String name;

    @NotBlank(message = "Họ và tên đầy đủ (fullName) không được để trống")
    @Size(max = 128, message = "Họ và tên đầy đủ không được vượt quá 128 ký tự")
    private String fullName;

    @NotBlank(message = "Tuổi (age) không được để trống")
    @Pattern(regexp = "^[0-9]{1,3}$", message = "Tuổi phải là số nguyên dương từ 1 đến 3 chữ số")
    private String age;

    @NotNull(message = "index_id không được để trống")
    @Positive(message = "index_id phải là số dương")
    private Integer indexId;

    @Valid
    private List<PlayerIndexRequestDTO> playerIndexes;
}
