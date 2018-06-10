package com.mtdhb.api.entity.view;

import java.math.BigDecimal;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/17
 */
public interface ReceivingTrendView {

    String getDate();
    
    BigDecimal getTotalPrice();
    
    long getCount();
}
