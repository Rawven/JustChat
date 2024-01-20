package www.raven.jc.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.util.CommonSerializable;

import java.io.Serializable;
import java.util.List;

/**
 * user register dto
 *
 * @author 刘家辉
 * @date 2023/12/02
 */

@Data
@Accessors(chain = true)
public class UserRegisterDTO extends CommonSerializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String email;
    private List<Integer> roleIds;
}
