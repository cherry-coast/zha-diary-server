package com.cherry.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Admin Login Parameter")
public class AdminLoginParam {

    @Schema(description = "Admin Username", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Admin Password", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
