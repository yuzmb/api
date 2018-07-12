/**
 * 
 */
package com.mtdhb.api.dto;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/10
 */
@Data
public class UserDTO {

    private Long id;
    private String avatar;
    private String name;
    private String mail;
    private String phone;
    private String token;
    private Boolean locked;

}
