package www.raven.jc.service;

import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.vo.AllInfoVO;
import www.raven.jc.entity.vo.InfoVO;
import www.raven.jc.entity.vo.RealAllInfoVO;

import java.util.List;

/**
 * info service
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
public interface InfoService {
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

    /**
     * query single info
     *
     * @param userId user id
     * @return {@link UserInfoDTO}
     */
    UserInfoDTO querySingleInfo(Integer userId);

    /**
     * query all info
     *
     * @return {@link List}<{@link UserInfoDTO}>
     */
    List<UserInfoDTO> queryAllInfo();

    /**
     * default info
     *
     * @param userId user id
     * @return {@link UserInfoDTO}
     */
    InfoVO defaultInfo(Integer userId);

    /**
     * query liked info list
     * query liked info list
     *
     * @param text   text
     * @param column column
     * @return {@link List}<{@link UserInfoDTO}>
     */
    List<UserInfoDTO> queryLikedInfoList(String column, String text);

    /**
     * query page user
     * @param page page
     * @return {@link List}<{@link AllInfoVO}>
     */
    RealAllInfoVO queryPageUser(Integer page);
}
