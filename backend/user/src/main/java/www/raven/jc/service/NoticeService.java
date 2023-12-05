package www.raven.jc.service;

import www.raven.jc.entity.vo.NoticeVO;

import java.util.List;

/**
 * notice service
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
public interface NoticeService {
    /**
     * load notice
     *
     * @return {@link List}<{@link NoticeVO}>
     */
    List<NoticeVO> loadNotice();
}
