package www.raven.jc.service;

import www.raven.jc.entity.model.CommentModel;
import www.raven.jc.entity.model.MomentModel;
import www.raven.jc.entity.po.Moment;
import www.raven.jc.entity.vo.MomentVO;

import java.util.List;

/**
 * social service
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
public interface SocialService {
    /**
     * release moment
     *
     * @param model model
     */
    void releaseMoment(MomentModel model);

    /**
     * delete moment
     *
     * @param momentId moment id
     */
    void deleteMoment(String momentId);

    /**
     * like moment
     *
     * @param momentId moment id
     */
    void likeMoment(String momentId);

    /**
     * comment moment
     *
     * @param model model
     */
    void commentMoment(CommentModel model);

    /**
     * query moment
     *
     * @return {@link List}<{@link Moment}>
     */
    List<MomentVO> queryMoment(int userId);
}
