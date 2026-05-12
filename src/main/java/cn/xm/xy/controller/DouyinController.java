package cn.xm.xy.controller;

import cn.xm.xy.common.result.R;
import cn.xm.xy.dto.douyin.DouyinTokenDTO;
import cn.xm.xy.dto.douyin.DouyinRequestDTO;
import cn.xm.xy.service.DouyinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/douyin")
@RequiredArgsConstructor
@Tag(name = "抖音平台", description = "抖音API中转调用")
public class DouyinController {

    private final DouyinService douyinService;

    @PostMapping("/auth")
    @Operation(summary = "获取access_token", description = "clientKey+clientSecret换取access_token，缓存2小时")
    public R<String> auth(@Valid @RequestBody DouyinTokenDTO dto) {
        return R.ok(douyinService.getAccessToken(dto));
    }

    @GetMapping("/invoke")
    @Operation(summary = "GET调用", description = "携带access_token转发GET请求")
    public R<String> get(DouyinRequestDTO dto) {
        return R.ok(douyinService.get(dto));
    }

    @PostMapping("/invoke")
    @Operation(summary = "POST调用", description = "携带access_token转发POST请求")
    public R<String> post(@Valid @RequestBody DouyinRequestDTO dto) {
        return R.ok(douyinService.post(dto));
    }
}
