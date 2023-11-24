package www.raven.jc.service;


import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;

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

    /**
     * profile upload
     *
     * @param profile profile
     */
    void setProfile(MultipartFile profile);

    /**
     * set signature
     *
     * @param signature signature
     */
    void setSignature(String signature);

    /**
     * set username
     *
     * @param username username
     */
    void setUsername(String username);
}
