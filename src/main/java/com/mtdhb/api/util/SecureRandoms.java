package com.mtdhb.api.util;

import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/09
 */
public class SecureRandoms {

    private static final int DEFAULT_HEX_LENGTH = 32;

    public static String nextHex() {
        return nextHex(DEFAULT_HEX_LENGTH);
    }

    /**
     * 生成用户指定长度十六进制随机字符串
     * 
     * @param length
     *            要生成的十六进制随机字符串的长度
     * @return 用户指定长度十六进制随机字符串
     */
    public static String nextHex(int length) {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[length + 1 >> 1];
        random.nextBytes(bytes);
        String hex = DatatypeConverter.printHexBinary(bytes);
        hex = hex.substring(0, length);
        return hex;
    }

}
