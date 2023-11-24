package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * room model
 *
 * @author 刘家辉
 * @date 2023/11/24
 */
@Data
@Accessors(chain = true)
public class RoomModel {
    private String name;
    private String description;
    private Integer maxPeople;
}
