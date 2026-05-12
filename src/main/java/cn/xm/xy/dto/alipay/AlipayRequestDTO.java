package cn.xm.xy.dto.alipay;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "支付宝业务请求")
public class AlipayRequestDTO {

    @NotBlank(message = "appId不能为空")
    @Schema(description = "应用AppId，用于关联token")
    private String appId;

    @NotBlank(message = "method不能为空")
    @Schema(description = "接口方法名，如 alipay.trade.query")
    private String method;

    @Schema(description = "业务参数")
    private Map<String, Object> bizContent;

    @Schema(description = "自定义请求头参数，用于功能扩展或自定义定义")
    private Map<String, String> headers;
}
