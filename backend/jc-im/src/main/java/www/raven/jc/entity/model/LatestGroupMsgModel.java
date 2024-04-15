package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * latest group msg model
 *
 * @author 刘家辉
 * @date 2024/02/27
 */
@Data
@Accessors(chain = true)
public class LatestGroupMsgModel {
    @NotNull(message = "roomId不能为空")
    @Min(value = 1, message = "roomId最小为1")
    private Integer roomId;
}
