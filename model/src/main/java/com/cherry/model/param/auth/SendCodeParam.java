package com.cherry.model.param.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Send Verification Code Parameter")
public class SendCodeParam {

    @Schema(description = "Email", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Verification Code Type (1: Register, 2: Login, 3: Update Info)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

}
