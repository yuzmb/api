/**
 * 
 */
package com.mtdhb.api.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/10
 */
@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 2084135877780190421L;

    private Long id;
    private String avatar;
    private String name;
    private String mail;
    private String phone;
    private String token;
    private Boolean locked;

}
