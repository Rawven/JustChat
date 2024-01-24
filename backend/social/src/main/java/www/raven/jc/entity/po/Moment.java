package www.raven.jc.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.List;

/**
 * moment
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Data
@Accessors(chain = true)
public class Moment {
    /**
     * 注意ObjectId的处理
     */
    @MongoId
    private ObjectId momentId;
    private Integer userId;
    private String content;
    private String img;
    private List<Like> likes;
    private List<Comment> comments;
    private Long timestamp;
}
