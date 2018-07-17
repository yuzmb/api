package com.mtdhb.api.cache;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;

/**
 * @author i@huangdenghe.com
 * @date 2018/05/28
 * @see org.springframework.cache.interceptor.SimpleKeyGenerator
 */
@Deprecated
public class SignatureKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        Object[] elements = new Object[params.length + 2];
        elements[0] = target.getClass().getName();
        elements[1] = method.getName();
        System.arraycopy(params, 0, elements, 2, params.length);
        return new SimpleKey(elements);
    }

}
