package cn.xm.xy.dto.douyin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "抖音业务请求")
public class DouyinRequestDTO {

    @NotBlank(message = "clientKey不能为空")
    @Schema(description = "ClientKey，用于关联access_token")
    private String clientKey;

    @NotBlank(message = "path不能为空")
    @Schema(description = "接口路径，如 /api/douyin/v1/video/list")
    private String path;

    @Schema(description = "请求参数")
    private Map<String, Object> params;

    @Schema(description = "自定义请求头参数，用于功能扩展或自定义定义")
    private Map<String, String> headers;
}
