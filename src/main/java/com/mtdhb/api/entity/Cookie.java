package com.mtdhb.api.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.mtdhb.api.constant.HttpService;
import com.mtdhb.api.constant.ThirdPartyApplication;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/03
 */
@Data
@Entity
public class Cookie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String value;
    @Enumerated
    private HttpService service;
    @Enumerated
    private ThirdPartyApplication application;
    private String openId;
    private String nickname;
    private String headImgUrl;
    private Long userId;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;

}
