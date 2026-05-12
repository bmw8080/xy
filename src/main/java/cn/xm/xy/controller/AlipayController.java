package cn.xm.xy.controller;

import cn.xm.xy.common.result.R;
import cn.xm.xy.dto.alipay.AlipayTokenDTO;
import cn.xm.xy.dto.alipay.AlipayRequestDTO;
import cn.xm.xy.service.AlipayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alipay")
@RequiredArgsConstructor
@Tag(name = "支付宝平台", description = "支付宝API中转调用")
public class AlipayController {

    private final AlipayService alipayService;

    @PostMapping("/auth")
    @Operation(summary = "注册凭据", description = "注册appId+privateKey，缓存2小时，后续调用自动签名")
    public R<String> auth(@Valid @RequestBody AlipayTokenDTO dto) {
        return R.ok(alipayService.getToken(dto));
    }

    @PostMapping("/invoke")
    @Operation(summary = "接口调用", description = "自动签名并转发到支付宝网关")
    public R<String> invoke(@Valid @RequestBody AlipayRequestDTO dto) {
        return R.ok(alipayService.execute(dto));
    }
}
