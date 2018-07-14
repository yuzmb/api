package com.mtdhb.api.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtdhb.api.constant.Constants;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/16
 */
public class MessageDigests {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
            logger.error("text={},algorithm={}", text, algorithm, e);
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
