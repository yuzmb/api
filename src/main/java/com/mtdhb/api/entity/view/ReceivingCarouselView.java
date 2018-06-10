package com.mtdhb.api.entity.view;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/22
 */
public interface ReceivingCarouselView {

    String getMail();

    int getApplication();

    BigDecimal getPrice();

    Timestamp getGmtModified();

}
