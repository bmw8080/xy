package cn.xm.xy.dto.wechat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "微信业务请求")
public class WeChatRequestDTO {

    @NotBlank(message = "appid不能为空")
    @Schema(description = "AppID，用于关联access_token")
    private String appid;

    @NotBlank(message = "path不能为空")
    @Schema(description = "接口路径，如 /cgi-bin/user/info")
    private String path;

    @Schema(description = "请求参数（GET作为query，POST作为body）")
    private Map<String, Object> params;

    @Schema(description = "自定义请求头参数，用于功能扩展或自定义定义")
    private Map<String, String> headers;
}
