package www.raven.jc.entity.po;

import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;

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
    private String receiverId;
    private Date timestamp;
}
