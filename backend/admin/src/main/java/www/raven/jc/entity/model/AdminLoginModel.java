package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * admin login model
 *
 * @author 刘家辉
 * @date 2023/11/27
 */
@Data
@Accessors(chain = true)
public class AdminLoginModel {
    private String publicKey;
    private String privateKey;
}
