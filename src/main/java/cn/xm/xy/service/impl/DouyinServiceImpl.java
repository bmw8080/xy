package cn.xm.xy.service.impl;

import cn.xm.xy.common.exception.BusinessException;
import cn.xm.xy.common.result.ResultCode;
import cn.xm.xy.dto.douyin.DouyinTokenDTO;
import cn.xm.xy.dto.douyin.DouyinRequestDTO;
import cn.xm.xy.service.DouyinService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DouyinServiceImpl implements DouyinService {

    private static final String BASE_URL = "https://developer.toutiao.com";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Cache<String, String> platformTokenCache;

    @Override
    public String getAccessToken(DouyinTokenDTO dto) {
        String cacheKey = "douyin:" + dto.getClientKey();
        String cached = platformTokenCache.getIfPresent(cacheKey);
        if (cached != null) {
            log.debug("抖音token缓存命中: clientKey={}", dto.getClientKey());
            return cached;
        }

        log.info("抖音获取access_token: clientKey={}", dto.getClientKey());

        try {
            Map<String, String> body = Map.of(
                    "client_key", dto.getClientKey(),
                    "client_secret", dto.getClientSecret(),
                    "grant_type", dto.getCode() != null ? "authorization_code" : "client_credential"
            );
            if (dto.getCode() != null) {
                body = Map.of(
                        "client_key", dto.getClientKey(),
                        "client_secret", dto.getClientSecret(),
                        "grant_type", "authorization_code",
                        "code", dto.getCode()
                );
            }

            String resp = restClient.post()
                    .uri(BASE_URL + "/oauth/access_token/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode node = objectMapper.readTree(resp);
            JsonNode data = node.path("data");
            if (data.has("access_token")) {
                String token = data.get("access_token").asText();
                platformTokenCache.put(cacheKey, token);
                return token;
            } else {
                throw new BusinessException(ResultCode.REMOTE_CALL_ERROR,
                        "抖音获取token失败: " + resp);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("抖音获取token异常: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "抖音获取token异常: " + e.getMessage());
        }
    }

    @Override
    public String get(DouyinRequestDTO dto) {
        String token = platformTokenCache.getIfPresent("douyin:" + dto.getClientKey());
        if (token == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID, "请先调用鉴权接口获取token");
        }
        String url = BASE_URL + dto.getPath() + (dto.getPath().contains("?") ? "&" : "?") + "access_token=" + token;
        log.info("抖音GET: {}", dto.getPath());

        try {
            var req = restClient.get().uri(url)
                    .header("Authorization", "Bearer " + token);
            if (dto.getHeaders() != null) {
                dto.getHeaders().forEach(req::header);
            }
            return req.retrieve().body(String.class);
        } catch (Exception e) {
            log.error("抖音GET失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "抖音接口调用失败: " + e.getMessage());
        }
    }

    @Override
    public String post(DouyinRequestDTO dto) {
        String token = platformTokenCache.getIfPresent("douyin:" + dto.getClientKey());
        if (token == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID, "请先调用鉴权接口获取token");
        }
        String url = BASE_URL + dto.getPath();
        log.info("抖音POST: {}", dto.getPath());

        try {
            var spec = restClient.post().uri(url)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON);
            if (dto.getHeaders() != null) {
                dto.getHeaders().forEach(spec::header);
            }
            if (dto.getParams() != null) {
                spec.body(dto.getParams());
            }
            return spec.retrieve().body(String.class);
        } catch (Exception e) {
            log.error("抖音POST失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "抖音接口调用失败: " + e.getMessage());
        }
    }
}
