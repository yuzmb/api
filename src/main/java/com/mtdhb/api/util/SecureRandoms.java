package com.mtdhb.api.util;

import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/09
 */
public class SecureRandoms {

    private static final int DEFAULT_STRING_LENGTH = 32;

    public static String nextHex() {
        return nextHex(DEFAULT_STRING_LENGTH);
    }

    public static String nextHex(int length) {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[length + 1 >> 1];
        random.nextBytes(bytes);
        String hex = DatatypeConverter.printHexBinary(bytes);
        hex = hex.substring(0, length);
        return hex;
    }

}
