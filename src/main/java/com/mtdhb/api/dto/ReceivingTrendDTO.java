package com.mtdhb.api.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/17
 */
@Data
public class ReceivingTrendDTO {

    private String date;
    private BigDecimal totalPrice;
    private long count;

}
