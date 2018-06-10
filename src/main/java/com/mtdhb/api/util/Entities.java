package com.mtdhb.api.util;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.apache.tomcat.util.codec.binary.Base64;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/28
 */
public class Entities {

    private final static Pattern PATTERN = Pattern.compile("^(\\d{3})\\d{4}(\\d{4})$");

    public static String digestUserPassword(String password, String salt) {
        return MessageDigests.digest(password + salt, MessageDigests.SHA_512);
    }

    public static String generateUserToken() {
        return SecureRandoms.nextHex(128);
    }

    public static String encodePhone(String phone) {
        return PATTERN.matcher(phone).replaceAll("$1****$2");
    }

    public static String encodeNickname(String nickname) {
        return Base64.encodeBase64String(nickname.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeNickname(String nickname) {
        if (nickname != null) {
            return new String(Base64.decodeBase64(nickname), StandardCharsets.UTF_8);
        }
        return null;
    }

    public static String generateVerificationCode() {
        return SecureRandoms.nextHex(128);
    }

}
