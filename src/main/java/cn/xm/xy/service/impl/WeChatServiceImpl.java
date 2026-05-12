package cn.xm.xy.service.impl;

import cn.xm.xy.common.exception.BusinessException;
import cn.xm.xy.common.result.ResultCode;
import cn.xm.xy.dto.wechat.WeChatTokenDTO;
import cn.xm.xy.dto.wechat.WeChatRequestDTO;
import cn.xm.xy.service.wechat.WeChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeChatServiceImpl implements WeChatService {

    private static final String BASE_URL = "https://api.weixin.qq.com";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Cache<String, String> platformTokenCache;

    @Override
    public String getAccessToken(WeChatTokenDTO dto) {
        String cacheKey = "wechat:" + dto.getAppid();
        String cached = platformTokenCache.getIfPresent(cacheKey);
        if (cached != null) {
            log.debug("微信token缓存命中: appid={}", dto.getAppid());
            return cached;
        }

        String url = BASE_URL + "/cgi-bin/token?grant_type=client_credential&appid="
                + dto.getAppid() + "&secret=" + dto.getSecret();
        log.info("微信获取access_token: appid={}", dto.getAppid());

        try {
            String resp = restClient.get().uri(url).retrieve().body(String.class);
            JsonNode node = objectMapper.readTree(resp);

            if (node.has("access_token")) {
                String token = node.get("access_token").asText();
                platformTokenCache.put(cacheKey, token);
                return token;
            } else {
                throw new BusinessException(ResultCode.REMOTE_CALL_ERROR,
                        "微信获取token失败: errcode=" + node.path("errcode").asText()
                                + " errmsg=" + node.path("errmsg").asText());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信获取token异常: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "微信获取token异常: " + e.getMessage());
        }
    }

    @Override
    public String get(WeChatRequestDTO dto) {
        String token = getAccessToken(new WeChatTokenDTO(dto.getAppid(), null));
        String url = BASE_URL + dto.getPath() + (dto.getPath().contains("?") ? "&" : "?") + "access_token=" + token;
        log.info("微信GET: {}", dto.getPath());

        try {
            var req = restClient.get().uri(url);
            if (dto.getHeaders() != null) {
                dto.getHeaders().forEach(req::header);
            }
            return req.retrieve().body(String.class);
        } catch (Exception e) {
            log.error("微信GET失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "微信接口调用失败: " + e.getMessage());
        }
    }

    @Override
    public String post(WeChatRequestDTO dto) {
        String token = getAccessToken(new WeChatTokenDTO(dto.getAppid(), null));
        String url = BASE_URL + dto.getPath() + (dto.getPath().contains("?") ? "&" : "?") + "access_token=" + token;
        log.info("微信POST: {}", dto.getPath());

        try {
            var spec = restClient.post().uri(url)
                    .contentType(MediaType.APPLICATION_JSON);
            if (dto.getHeaders() != null) {
                dto.getHeaders().forEach(spec::header);
            }
            if (dto.getParams() != null) {
                spec.body(dto.getParams());
            }
            return spec.retrieve().body(String.class);
        } catch (Exception e) {
            log.error("微信POST失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "微信接口调用失败: " + e.getMessage());
        }
    }
}
