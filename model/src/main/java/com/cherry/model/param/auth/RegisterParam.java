package com.cherry.model.param.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Registration Request Parameter")
public class RegisterParam {

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Nickname")
    private String nickname;

}
