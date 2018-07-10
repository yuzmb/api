package com.mtdhb.api.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.mtdhb.api.constant.e.ReceivingStatus;
import com.mtdhb.api.constant.e.ThirdPartyApplication;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/10
 */
@Data
@Entity
public class Receiving {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String urlKey;
    private String url;
    private String phone;
    @Enumerated
    private ThirdPartyApplication application;
    @Enumerated
    private ReceivingStatus status;
    private String message;
    private String nickname;
    private BigDecimal price;
    private String date;
    private Long userId;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;

}
