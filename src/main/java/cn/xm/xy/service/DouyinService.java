package cn.xm.xy.service;

import cn.xm.xy.dto.douyin.DouyinTokenDTO;
import cn.xm.xy.dto.douyin.DouyinRequestDTO;

public interface DouyinService {

    /**
     * 抖音鉴权：clientKey+clientSecret → access_token
     * 缓存2小时
     */
    String getAccessToken(DouyinTokenDTO dto);

    /**
     * 转发GET请求
     */
    String get(DouyinRequestDTO dto);

    /**
     * 转发POST请求
     */
    String post(DouyinRequestDTO dto);
}
