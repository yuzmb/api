package com.mtdhb.api.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/22
 */
@Data
public class ReceivingPieDTO {

    private BigDecimal price;
    private long proportion;

}
