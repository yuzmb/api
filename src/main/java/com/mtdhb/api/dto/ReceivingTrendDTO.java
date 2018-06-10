package com.mtdhb.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/17
 */
@Data
public class ReceivingTrendDTO implements Serializable {

    private static final long serialVersionUID = -2695002604795431382L;

    private String date;
    private BigDecimal totalPrice;
    private long count;

}
