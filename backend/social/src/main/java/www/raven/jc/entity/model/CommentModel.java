package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

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
    private String commentId;
    private String text;
}
