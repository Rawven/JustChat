package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Like;

@Data
@Accessors(chain = true)
public class LikeVO {
    private String id;
    private UserInfoDTO userInfoDTO;
    private Long timestamp;

    public LikeVO(Like like, UserInfoDTO userInfoDTO) {
        this.id = like.getId();
        this.userInfoDTO = userInfoDTO;
        this.timestamp = like.getTimestamp();
    }
}
