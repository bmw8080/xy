package cn.xm.xy.service.impl;

import cn.xm.xy.common.exception.BusinessException;
import cn.xm.xy.common.result.ResultCode;
import cn.xm.xy.dto.alipay.AlipayTokenDTO;
import cn.xm.xy.dto.alipay.AlipayRequestDTO;
import cn.xm.xy.service.AlipayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlipayServiceImpl implements AlipayService {

    private static final String GATEWAY = "https://openapi.alipay.com/gateway.do";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Cache<String, String> platformTokenCache;

    @Override
    public String getToken(AlipayTokenDTO dto) {
        String cacheKey = "alipay:" + dto.getAppId();
        String cached = platformTokenCache.getIfPresent(cacheKey);
        if (cached != null) {
            log.debug("支付宝token缓存命中: appId={}", dto.getAppId());
            return cached;
        }

        // 支付宝无需单独获取token，用私钥签名即可调用
        // 这里缓存 privateKey 供后续签名使用
        log.info("支付宝注册凭据: appId={}", dto.getAppId());
        platformTokenCache.put(cacheKey, dto.getPrivateKey());
        return dto.getPrivateKey();
    }

    @Override
    public String execute(AlipayRequestDTO dto) {
        String cacheKey = "alipay:" + dto.getAppId();
        String privateKey = platformTokenCache.getIfPresent(cacheKey);
        if (privateKey == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID, "请先调用鉴权接口注册凭据");
        }

        log.info("支付宝接口调用: method={}", dto.getMethod());

        try {
            Map<String, String> params = buildCommonParams(dto.getAppId(), dto.getMethod());
            if (dto.getBizContent() != null) {
                params.put("biz_content", objectMapper.writeValueAsString(dto.getBizContent()));
            }

            // 简化签名：将参数排序后拼接（实际生产环境应用RSA2签名）
            String sign = simplifySign(params, privateKey);
            params.put("sign", sign);

            StringBuilder url = new StringBuilder(GATEWAY).append("?");
            params.forEach((k, v) -> url.append(k).append("=")
                    .append(URLEncoder.encode(v, StandardCharsets.UTF_8)).append("&"));

            return restClient.post()
                    .uri(url.toString())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .headers(h -> {
                        if (dto.getHeaders() != null) {
                            dto.getHeaders().forEach(h::set);
                        }
                    })
                    .retrieve()
                    .body(String.class);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("支付宝接口调用失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "支付宝接口调用失败: " + e.getMessage());
        }
    }

    private Map<String, String> buildCommonParams(String appId, String method) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("app_id", appId);
        params.put("method", method);
        params.put("charset", "utf-8");
        params.put("sign_type", "RSA2");
        params.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        params.put("version", "1.0");
        params.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
        return params;
    }

    /**
     * 简化签名 - 生产环境请替换为 RSA2 签名
     */
    private String simplifySign(Map<String, String> params, String privateKey) {
        String content = params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
        // TODO: 替换为 RSA2 签名
        return "placeholder_sign_for_" + content.hashCode();
    }
}
