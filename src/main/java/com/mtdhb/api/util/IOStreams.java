package com.mtdhb.api.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.mtdhb.api.constant.Constants;

/**
 * @author i@huangdenghe.com
 * @date 2018/02/01
 */
public class IOStreams {

    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        byte[] buffer = new byte[Constants.BUFFER_SIZE];
        int n;
        while ((n = in.read(buffer)) > 0) {
            sink.write(buffer, 0, n);
        }
        return sink.toByteArray();
    }

}
