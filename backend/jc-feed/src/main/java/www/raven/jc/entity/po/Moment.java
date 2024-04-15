package www.raven.jc.entity.po;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.model.CommonSerializable;

import java.util.List;

/**
 * moment
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@JsonSerialize
public class Moment extends CommonSerializable {
    /**
     * 注意ObjectId的处理
     */
    @MongoId
    private ObjectId momentId;
    private UserInfoDTO userInfo;
    private String content;
    private String img;
    private List<Like> likes;
    private List<Comment> comments;
    private Long timestamp;
}
