package www.raven.jc.entity.vo;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;

import java.util.List;
import www.raven.jc.entity.po.Moment;

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
    private List<Like> likes;
    private List<Comment> comments;
    private Long timestamp;
    public MomentVO(Moment moment) {
        this.momentId = moment.getMomentId().toHexString();
        this.userInfo = moment.getUserInfo();
        this.content = moment.getContent();
        this.img = moment.getImg();
        this.likes = moment.getLikes();
        this.comments = moment.getComments();
        this.timestamp = moment.getTimestamp();
    }
}
