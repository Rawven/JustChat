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
    String registerCommonRole(RegisterModel registerModel);

    /**
     * register admin role
     *
     * @param registerModel register model
     * @return {@link String}
     */
    String registerAdminRole(RegisterModel registerModel);


    /**
     * refresh token
     *
     * @param token token
     * @return {@link String}
     */
    String refreshToken(String token);

    /**
     * logout
     *
     * @param token token
     */
    void logout(String token);
}
