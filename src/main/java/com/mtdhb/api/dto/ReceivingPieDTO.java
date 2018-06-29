package com.mtdhb.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/22
 */
@Data
public class ReceivingPieDTO implements Serializable {

    private static final long serialVersionUID = -3643671463667810717L;

    private BigDecimal price;
    private long proportion;

}
