package com.mtdhb.api.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import com.mtdhb.api.constant.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/16
 */
@Slf4j
public class MessageDigests {

    public static final String MD5 = "MD5";
    /**
     * 在java 6中，“SHA”是“SHA-1”的简称，两种算法名称等同。
     */
    public static final String SHA = "SHA";
    public static final String SHA_256 = "SHA-256";
    public static final String SHA_384 = "SHA-384";
    public static final String SHA_512 = "SHA-512";

    public static String digest(String text, String algorithm) {
        try {
            return digest(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), algorithm);
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error("text={},algorithm={}", text, algorithm, e);
        }
        return null;
    }

    private static String digest(InputStream is, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        DigestInputStream dis = new DigestInputStream(is, md);
        byte[] b = new byte[Constants.BUFFER_SIZE];
        while (dis.read(b, 0, b.length) != -1)
            ;
        return DatatypeConverter.printHexBinary(md.digest());
    }

}
