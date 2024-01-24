package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;

import java.util.List;

/**
 * moment vo
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Data
@Accessors(chain = true)
public class MomentVO {
    private String momentId;
    private Integer userId;
    private String content;
    private String img;
    private List<Like> likes;
    private List<Comment> comments;
    private Long timestamp;
}
