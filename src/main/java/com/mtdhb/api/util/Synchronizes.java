package com.mtdhb.api.util;

import com.mtdhb.api.constant.e.ThirdPartyApplication;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/18
 */
public class Synchronizes {

    public static String buildReceivingLock(String urlKey, ThirdPartyApplication application) {
        return joinIntern("RECEIVING", urlKey, application.name());
    }

    public static String buildUserReceiveLock(ThirdPartyApplication application, long userId) {
        return joinIntern("USER_RECEIVE", application.name(), Long.toString(userId));
    }

    private static String joinIntern(String... elements) {
        return String.join("::", elements).intern();
    }

}
