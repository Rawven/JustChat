package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * real all info vo
 *
 * @author 刘家辉
 * @date 2023/12/01
 */

@Data
@Accessors(chain = true)
public class RealAllInfoVO {
    private List<AllInfoVO> users;
    private Integer total;
}
