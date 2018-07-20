package com.mtdhb.api.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mtdhb.api.autoconfigure.NodejsProperties;
import com.mtdhb.api.constant.e.ThirdPartyApplication;
import com.mtdhb.api.dto.nodejs.CookieCheckDTO;
import com.mtdhb.api.dto.nodejs.RedPacketDTO;
import com.mtdhb.api.dto.nodejs.ResultDTO;
import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.exception.BusinessException;
import com.mtdhb.api.service.NodejsService;
import com.mtdhb.api.util.Connections;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/28
 */
@Service
public class NodejsServiceImpl implements NodejsService {

    @Autowired
    private NodejsProperties nodejsProperties;

    @Override
    public CookieCheckDTO checkCookie(String cookieValue, ThirdPartyApplication application) throws IOException {
        String spec = nodejsProperties.getUrl() + nodejsProperties.getCheckCookie();
        Map<String, Object> arg = new HashMap<>();
        arg.put("cookie", cookieValue);
        arg.put("application", application.ordinal());
        ResultDTO<CookieCheckDTO> resultDTO = Connections.post(spec, arg,
                new TypeReference<ResultDTO<CookieCheckDTO>>() {
                });
        if (resultDTO.getCode() != 0) {
            throw new BusinessException(resultDTO.getCode(), resultDTO.getMessage(),
                    "cookieValue={}, application={}, resultDTO={}", cookieValue, application, resultDTO);
        }
        return resultDTO.getData();
    }

    @Override
    public ResultDTO<RedPacketDTO> getHongbao(String url, String phone, ThirdPartyApplication application, long limit,
            List<Cookie> cookies) throws IOException {
        String spec = nodejsProperties.getUrl() + nodejsProperties.getGetHongbao();
        Map<String, Object> arg = new HashMap<>();
        arg.put("url", url);
        arg.put("mobile", phone);
        arg.put("application", application.ordinal());
        arg.put("limit", limit);
        arg.put("cookies", cookies);
        ResultDTO<RedPacketDTO> resultDTO = Connections.post(spec, arg, new TypeReference<ResultDTO<RedPacketDTO>>() {
        });
        return resultDTO;
    }

}
