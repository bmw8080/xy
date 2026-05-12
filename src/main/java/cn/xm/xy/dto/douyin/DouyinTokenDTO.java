package cn.xm.xy.dto.douyin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "抖音凭据 - 获取access_token")
public class DouyinTokenDTO {

    @NotBlank(message = "clientKey不能为空")
    @Schema(description = "应用ClientKey", example = "aw1234567890")
    private String clientKey;

    @NotBlank(message = "clientSecret不能为空")
    @Schema(description = "应用ClientSecret")
    private String clientSecret;

    @Schema(description = "授权码（可选，用户授权模式时传入）")
    private String code;
}
