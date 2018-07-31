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

    /**
     * 从给定输入流中读取所有字节。该方法不关闭输入流且不适用于读取大量数据
     * 
     * @param in
     *            输入流
     * @return 包含从输入流中读取的所有字节的字节数组
     * @throws IOException
     *             如果发生 I/O 错误
     */
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
