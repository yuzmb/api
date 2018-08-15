package com.mtdhb.api.dto.nodejs;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/13
 */
@Data
public class RedPacketResultDTO {

    private String id;
    private String nickname;
    private BigDecimal price;
    private String date;
    private Integer type;

}
