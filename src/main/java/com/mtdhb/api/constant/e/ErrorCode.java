package com.mtdhb.api.constant.e;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/11
 */
@AllArgsConstructor
@Getter
@ToString
public enum ErrorCode {

    AUTHENTICATION_EXCEPTION(10000, "认证异常！"),
    ACCOUNT_OR_PASSWORD_ERROR(10001, "账号或密码错误！"),
    MAIL_VERIFICATION_EXCEPTION(10002, "校验码时效已过或已被使用或校验码不正确！"),
    CAPTCHA_ERROR(10003, "请输入正确的验证码！"),
    MAIL_EXIST(10004, "邮箱已被注册！"),
    COOKIE_EXIST(10005, "Cookie 已存在！"),
    THIRDPARTYAPPLICATION_EXCEPTION(10006, "第三方应用参数不正确！"),
    COOKIE_NOT_EXIST(10007, "要删除的 cookie 不存在！"),
    COOKIE_DELETE_FAILURE(10008, "存在领取中的红包，不允许删除 cookie！"),
    COOKIE_DELETE_EXCEPTION(10009, "可用次数不足 5 次，不允许删除 cookie！"),
    COOKIE_INSUFFICIENT(10010, "服务器可用 cookie 不足！"),
    USER_RECEIVE_WAIT(10011, "当前用户已有红包在领取中，请等待上一个红包领取完成再操作！"),
    RECEIVE_ING(10012, "该红包链接正在领取中！"),
    URL_ERROR(10013, "红包链接不正确！"),
    RECEIVING_EXIST(10014, "该红包链接已被领取！"),
    AVAILABLE_INSUFFICIENT(10015, "美团或饿了么的可用次数不足（小于 2 次），不能领取对应的红包！"),
    RECEIVING_NOT_EXIST(10016, "请求的数据不存在！"),
    SYSTEM_MAINTENANCE(10017, "领取失败，%s 至 %s 为系统维护时间。维护原因：致敬 12306"),
    MAIL_NOT_EXIST(10018, "邮箱不存在！"),
    USER_LOCKED(10019, "账号已被封禁！"),
    COOKIE_MEITUAN_DELETE_FAILURE(10020, "由于目前可用的美团 cookie 均已绝版，为了防止用户误删，暂时禁止删除。如确定要删除，请联系管理员！"),
    MAIL_ON_BLACKLIST(10021, "禁止使用临时邮箱注册！"),
    PHONE_ERROR(10022, "请填写 11 位手机号码或留空不填！");

    private int code;
    private String message;

}
