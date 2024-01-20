package www.raven.jc.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;


/**
 * message
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Data
@Accessors(chain = true)
public class Message {
    /**
     * 注意ObjectId的处理
     */
    @MongoId
    private ObjectId messageId;
    private Integer senderId;
    private String content;
    private String type;
    @Indexed
    private Integer receiverId;
    private Date timestamp;
}
