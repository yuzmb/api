package com.mtdhb.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author i@huangdenghe.com
 * @date 2018/02/01
 */
public class Connections {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static int DEFAULT_TIME_OUT = 1 * 60 * 1000;

    public static String getRedirectURL(String spec) throws IOException {
        logger.info("Redirect: spec={}", spec);
        HttpURLConnection connection = (HttpURLConnection) openConnection(spec);
        // 禁止重定向
        connection.setInstanceFollowRedirects(false);
        String location = connection.getHeaderField(HttpHeaders.LOCATION);
        logger.info("Redirect: location={}", location);
        return location;

    }

    public static <T> T post(String spec, Object arg, TypeReference<?> valueTypeRef) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String parameter = objectMapper.writeValueAsString(arg);
        logger.info("Node.js request: spec={}, parameter={}", spec, parameter);
        URLConnection connection = openConnection(spec);
        connection.addRequestProperty(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
        connection.setConnectTimeout(DEFAULT_TIME_OUT);
        connection.setReadTimeout(DEFAULT_TIME_OUT);
        connection.setDoOutput(true);
        try (OutputStream out = connection.getOutputStream();) {
            out.write(parameter.getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
        try (InputStream in = connection.getInputStream()) {
            byte[] b = IOStreams.readAllBytes(in);
            String body = new String(b, StandardCharsets.UTF_8);
            logger.info("Node.js response: body={}", body);
            return objectMapper.readValue(body, valueTypeRef);
        }
    }

    private static URLConnection openConnection(String spec) throws IOException {
        URL url = new URL(spec);
        return url.openConnection();
    }

}
