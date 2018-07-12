package com.mtdhb.api.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/26
 */
@Data
public class ReceivingCarouselDTO {

    private String mail;
    private Integer application;
    private BigDecimal price;
    private Timestamp gmtModified;

}
