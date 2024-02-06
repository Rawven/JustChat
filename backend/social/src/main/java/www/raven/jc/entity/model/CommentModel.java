package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

/**
 * comment model
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Data
@Accessors(chain = true)
public class CommentModel {
    private String momentId;
    private Integer momentUserId;
    private String text;
}
