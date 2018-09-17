package com.mtdhb.api.service;

import java.io.IOException;
import java.util.List;

import com.mtdhb.api.constant.e.ThirdPartyApplication;
import com.mtdhb.api.dto.nodejs.CookieCheckDTO;
import com.mtdhb.api.dto.nodejs.RedPacketDTO;
import com.mtdhb.api.dto.nodejs.ResultDTO;
import com.mtdhb.api.entity.Cookie;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/28
 */
public interface NodejsService {

    CookieCheckDTO checkCookie(String cookieValue, ThirdPartyApplication application, long userId) throws IOException;

    ResultDTO<RedPacketDTO> getHongbao(String url, String phone, ThirdPartyApplication application, long limit,
            List<Cookie> cookies) throws IOException;

}
