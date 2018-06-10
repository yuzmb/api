package com.mtdhb.api.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.mtdhb.api.constant.CookieStatus;
import com.mtdhb.api.constant.ThirdPartyApplication;

import lombok.Data;

@Data
@Entity
public class CookieMark {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * TODO 冗余字段
     */
    @Enumerated
    private ThirdPartyApplication application;
    @Enumerated
    private CookieStatus status;
    /**
     * TODO 冗余字段
     */
    private Long userId;
    private Long cookieId;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;

}
