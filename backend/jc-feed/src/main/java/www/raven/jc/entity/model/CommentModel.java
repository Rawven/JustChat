package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * comment model
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Data
@Accessors(chain = true)
public class CommentModel {
    @NotBlank(message = "momentId不能为空")
    private String momentId;
    @NotNull(message = "momentUserId不能为空")
    @Min(value = 1, message = "momentUserId最小为1")
    private Integer momentUserId;
    @NotNull(message = "momentTimeStamp不能为空")
    @Min(value = 1, message = "momentTimeStamp最小为1")
    private Long momentTimeStamp;
    @NotBlank(message = "commentId不能为空")
    private String commentId;
    @NotNull(message = "commentUserId不能为空")
    private String text;
}
