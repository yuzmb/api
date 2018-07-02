package com.mtdhb.api.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.mtdhb.api.constant.e.CookieStatus;
import com.mtdhb.api.constant.e.ThirdPartyApplication;

import lombok.Data;

@Data
@Entity
public class CookieMark {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated
    private ThirdPartyApplication application;
    @Enumerated
    private CookieStatus status;
    private Long userId;
    private Long cookieId;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;

}
