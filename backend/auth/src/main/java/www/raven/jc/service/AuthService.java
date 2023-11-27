package www.raven.jc.service;


import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;

/**
 * account service
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
public interface AuthService {
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
    String register(RegisterModel registerModel);


}
