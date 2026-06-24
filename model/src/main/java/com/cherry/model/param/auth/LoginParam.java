package com.cherry.model.param.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Login Request Parameter")
public class LoginParam {

    @Schema(description = "Login Type (1: Password, 2: Email Code)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer loginType;

    @Schema(description = "Username (Required for Password Login)")
    private String username;

    @Schema(description = "Password (Required for Password Login)")
    private String password;

    @Schema(description = "Email (Required for Email Login)")
    private String email;

    @Schema(description = "Verification Code (Required for Email Login)")
    private String code;

}
