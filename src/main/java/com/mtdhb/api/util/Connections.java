package com.mtdhb.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author i@huangdenghe.com
 * @date 2018/02/01
 */
@Slf4j
public class Connections {

    private static final int DEFAULT_TIME_OUT = 1 * 60 * 1000;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static String getRedirectURL(String spec) throws IOException {
        log.info("Redirect: spec={}", spec);
        HttpURLConnection connection = (HttpURLConnection) openConnection(spec);
        // 禁止重定向
        connection.setInstanceFollowRedirects(false);
        int responseCode = connection.getResponseCode();
        log.info("Redirect: responseCode={}", responseCode);
        String location = null;
        if (responseCode == HttpStatus.FOUND.value()) {
            location = connection.getHeaderField(HttpHeaders.LOCATION);
        }
        log.info("Redirect: location={}", location);
        return location;

    }

    public static <T> T post(String spec, Object arg, TypeReference<?> valueTypeRef) throws IOException {
        String parameter = MAPPER.writeValueAsString(arg);
        log.info("Node.js request: spec={}, parameter={}", spec, parameter);
        URLConnection connection = openConnection(spec);
        connection.addRequestProperty(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
        connection.setDoOutput(true);
        try (OutputStream out = connection.getOutputStream();) {
            out.write(parameter.getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
        try (InputStream in = connection.getInputStream()) {
            byte[] b = IOStreams.readAllBytes(in);
            String body = new String(b, StandardCharsets.UTF_8);
            log.info("Node.js response: body={}", body);
            return MAPPER.readValue(body, valueTypeRef);
        }
    }

    private static URLConnection openConnection(String spec) throws IOException {
        URL url = new URL(spec);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(DEFAULT_TIME_OUT);
        connection.setReadTimeout(DEFAULT_TIME_OUT);
        return connection;
    }

}
