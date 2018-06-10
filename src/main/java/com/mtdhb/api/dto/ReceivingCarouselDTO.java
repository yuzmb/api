package com.mtdhb.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/26
 */
@Data
public class ReceivingCarouselDTO implements Serializable {

    private static final long serialVersionUID = -5501378056790203922L;

    private String mail;
    private Integer application;
    private BigDecimal price;
    private Timestamp gmtModified;

}
