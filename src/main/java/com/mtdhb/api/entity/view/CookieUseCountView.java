package com.mtdhb.api.entity.view;

import java.sql.Timestamp;

import com.mtdhb.api.constant.e.HttpService;
import com.mtdhb.api.constant.e.ThirdPartyApplication;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/15
 */
public interface CookieUseCountView {

    long getId();

    String getValue();

    HttpService getService();

    ThirdPartyApplication getApplication();

    String getOpenId();

    String getNickname();

    String getHeadImgUrl();

    long getUserId();

    Timestamp getGmtCreate();

    Timestamp getGmtModified();

    long getCount();

}
