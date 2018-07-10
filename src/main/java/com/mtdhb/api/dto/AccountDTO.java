package com.mtdhb.api.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/08
 */
@Data
public class AccountDTO {

    @NotNull
    @Size(min = 6, max = 16)
    private String password;
    @NotEmpty
    private String verificationCode;

}
