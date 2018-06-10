/**
 * 
 */
package com.mtdhb.api.service;

import com.mtdhb.api.constant.ThirdPartyApplication;
import com.mtdhb.api.dto.AccountDTO;
import com.mtdhb.api.dto.UserDTO;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/02
 */
public interface UserService {

    UserDTO loginByMail(String account, String password);

    UserDTO registerByMail(AccountDTO accountDTO);

    /**
     * 发送注册邮件
     * 
     * @param mail
     */
    void sendRegisterMail(String mail);

    UserDTO resetPassword(AccountDTO accountDTO);

    /**
     * 发送重置密码邮件
     * 
     * @param mail
     */
    void sendResetPasswordMail(String mail);

    UserDTO getByToken(String token);

    long getAvailable(ThirdPartyApplication application, long userId);

}
