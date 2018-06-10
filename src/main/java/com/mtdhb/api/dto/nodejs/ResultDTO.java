package com.mtdhb.api.dto.nodejs;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/06
 */
@Data
public class ResultDTO<T> {

    public int code;
    public String message;
    public T data;

}
