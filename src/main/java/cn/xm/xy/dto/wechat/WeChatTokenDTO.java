package cn.xm.xy.dto.wechat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "微信凭据 - 获取access_token")
public class WeChatTokenDTO {

    @NotBlank(message = "appid不能为空")
    @Schema(description = "小程序/公众号AppID", example = "wx1234567890")
    private String appid;

    @NotBlank(message = "secret不能为空")
    @Schema(description = "小程序/公众号AppSecret")
    private String secret;
}
