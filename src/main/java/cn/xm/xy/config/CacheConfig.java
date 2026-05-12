package cn.xm.xy.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    /**
     * 各平台 access_token 缓存
     * key: platform:credentialId (如 wechat:wx123)
     * value: access_token 字符串
     */
    @Bean
    public Cache<String, String> platformTokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build();
    }
}
