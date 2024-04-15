package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * room model
 *
 * @author 刘家辉
 * @date 2023/11/24
 */
@Data
@Accessors(chain = true)
public class RoomModel {
    @NotBlank(message = "name不能为空")
    private String name;
    @NotBlank(message = "description不能为空")
    private String description;
    @NotNull(message = "人数不能为空")
    @Min(value = 1, message = "profile最小为1")
    private Integer maxPeople;
}
