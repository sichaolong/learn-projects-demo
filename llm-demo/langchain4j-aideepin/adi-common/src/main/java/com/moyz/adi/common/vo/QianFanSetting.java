package com.moyz.adi.common.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QianFanSetting extends CommonAiPlatformSetting {

    private String apiKey;

    private String secretKey;
}
