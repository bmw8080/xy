package cn.xm.xy.controller.wechat;

import cn.xm.xy.common.result.R;
import cn.xm.xy.dto.wechat.WeChatTokenDTO;
import cn.xm.xy.dto.wechat.WeChatRequestDTO;
import cn.xm.xy.service.wechat.WeChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wechat")
@RequiredArgsConstructor
@Tag(name = "微信平台", description = "微信API中转调用")
public class WeChatController {

    private final WeChatService weChatService;

    @PostMapping("/auth")
    @Operation(summary = "获取access_token", description = "appid+secret换取access_token，缓存2小时")
    public R<String> auth(@Valid @RequestBody WeChatTokenDTO dto) {
        return R.ok(weChatService.getAccessToken(dto));
    }

    @GetMapping("/invoke")
    @Operation(summary = "GET调用", description = "携带access_token转发GET请求")
    public R<String> get(WeChatRequestDTO dto) {
        return R.ok(weChatService.get(dto));
    }

    @PostMapping("/invoke")
    @Operation(summary = "POST调用", description = "携带access_token转发POST请求")
    public R<String> post(@Valid @RequestBody WeChatRequestDTO dto) {
        return R.ok(weChatService.post(dto));
    }
}
