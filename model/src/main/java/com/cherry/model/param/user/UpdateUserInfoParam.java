package com.cherry.model.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Update User Info Parameter")
public class UpdateUserInfoParam {

    @Schema(description = "Avatar URL")
    private String avatar;

    @Schema(description = "Username")
    private String username;

    @Schema(description = "Password (Optional, requires email and code if provided)")
    private String password;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Verification Code")
    private String code;

}
