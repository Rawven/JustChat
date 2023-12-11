package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * room real vo
 * room real vo
 *
 * @author 刘家辉
 * @date 2023/11/24
 */

@Data
@Accessors(chain = true)
public class RealRoomVO {
    private List<DisplayRoomVO> rooms;
    private Integer total;
}

