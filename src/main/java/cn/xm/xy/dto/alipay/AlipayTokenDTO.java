package cn.xm.xy.dto.alipay;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "支付宝凭据 - 获取token")
public class AlipayTokenDTO {

    @NotBlank(message = "appId不能为空")
    @Schema(description = "支付宝应用AppId", example = "2021001234567890")
    private String appId;

    @NotBlank(message = "privateKey不能为空")
    @Schema(description = "应用私钥(RSA2)")
    private String privateKey;

    @Schema(description = "支付宝公钥（可选，验签用）")
    private String alipayPublicKey;
}
