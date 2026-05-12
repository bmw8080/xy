package cn.xm.xy.service;

import cn.xm.xy.dto.alipay.AlipayTokenDTO;
import cn.xm.xy.dto.alipay.AlipayRequestDTO;

public interface AlipayService {

    /**
     * 支付宝鉴权：appId+privateKey → 生成签名token
     * 缓存2小时
     */
    String getToken(AlipayTokenDTO dto);

    /**
     * 转发接口调用
     */
    String execute(AlipayRequestDTO dto);
}
