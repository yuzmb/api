package com.mtdhb.api.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.mtdhb.api.constant.e.ReceivingStatus;
import com.mtdhb.api.constant.e.ReceivingType;
import com.mtdhb.api.constant.e.ThirdPartyApplication;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/10
 */
@Data
public class ReceivingDTO {

    private Long id;
    private String urlKey;
    private String url;
    private String phone;
    private ThirdPartyApplication application;
    private ReceivingType type;
    private ReceivingStatus status;
    private BigDecimal price;
    private String message;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;

}
