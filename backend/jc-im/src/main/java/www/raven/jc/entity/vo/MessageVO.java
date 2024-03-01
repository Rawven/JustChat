package www.raven.jc.entity.vo;

import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Message;
import www.raven.jc.model.CommonSerializable;
import www.raven.jc.util.MongoUtil;

/**
 * message vo
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class MessageVO extends CommonSerializable {
    private Date time;
    private String text;
    private UserInfoDTO userInfoDTO;
    private Integer belongId;

    public MessageVO(Message message) {
        this.time = message.getTimestamp();
        this.text = message.getContent();
        this.userInfoDTO = message.getSender();
        this.belongId= MongoUtil.resolve(message.getReceiverId(),message.getSender().getUserId());

    }

}
