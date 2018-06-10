package com.mtdhb.api.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/01
 */
@Data
public class CookieRankDTO  implements Serializable {
    
    private static final long serialVersionUID = 855436697546208107L;
    
    private long ranking;
    private long userId;
    private long count;
    
}
