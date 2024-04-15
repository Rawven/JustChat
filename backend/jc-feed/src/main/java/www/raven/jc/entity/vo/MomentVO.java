package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Moment;

import java.io.Serializable;
import java.util.List;

/**
 * moment vo
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Data
@Accessors(chain = true)
public class MomentVO implements Serializable {
    private String momentId;
    private UserInfoDTO userInfo;
    private String content;
    private String img;
    private List<LikeVO> likes;
    private List<CommentVO> comments;
    private Long timestamp;

}
