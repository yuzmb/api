package com.mtdhb.api.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.mtdhb.api.constant.e.ThirdPartyApplication;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/14
 */
@Data
@Entity
public class CookieCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated
    private ThirdPartyApplication application;
    private String openId;
    /**
     * TODO 冗余字段
     */
    private Long userId;
    private Long cookieId;
    private Long receivingId;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;

}
