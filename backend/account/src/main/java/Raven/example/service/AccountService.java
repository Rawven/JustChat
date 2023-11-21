package Raven.example.service;

import Raven.example.entity.model.LoginModel;
import Raven.example.entity.model.RegisterModel;
import org.springframework.stereotype.Service;

/**
 * account service
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
public interface AccountService {
    /**
     * login
     *
     * @param loginModel login model
     * @return token
     */
    String login(LoginModel loginModel);

    /**
     * register
     *
     * @param registerModel register model
     * @return {@link String}
     */
    String register (RegisterModel registerModel);
}
