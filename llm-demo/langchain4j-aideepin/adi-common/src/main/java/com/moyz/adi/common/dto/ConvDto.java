package com.moyz.adi.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConvDto {

    private String uuid;

    @NotBlank
    private String title;

    private Integer tokens;

    @Schema(title = "set the system message to ai, ig: you are a lawyer")
    private String aiSystemMessage;

    private Boolean understandContextEnable;
}
