package com.mtdhb.api.dto.nodejs;

import java.util.List;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/13
 */
@Data
public class RedPacketDTO {

    private RedPacketResultDTO result;
    private List<CookieUseStatusDTO> cookies;
    
}
