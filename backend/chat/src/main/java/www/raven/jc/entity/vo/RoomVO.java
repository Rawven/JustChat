package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * room vo
 *
 * @author 刘家辉
 * @date 2023/11/24
 */

@Data
@Accessors(chain = true)
public class RoomVO {
    private String roomName;
    private String roomDescription;
    private String founderName;
    private Integer maxPeople;
}
