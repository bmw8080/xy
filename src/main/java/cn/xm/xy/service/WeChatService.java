package cn.xm.xy.service.wechat;

import cn.xm.xy.dto.wechat.WeChatTokenDTO;
import cn.xm.xy.dto.wechat.WeChatRequestDTO;

public interface WeChatService {

    /**
     * 微信鉴权：appid+secret → access_token
     * 缓存2小时，有效期内直接返回
     */
    String getAccessToken(WeChatTokenDTO dto);

    /**
     * 转发GET请求
     */
    String get(WeChatRequestDTO dto);

    /**
     * 转发POST请求
     */
    String post(WeChatRequestDTO dto);
}
