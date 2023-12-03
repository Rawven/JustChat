package www.raven.jc.constant;

import java.util.HashMap;

/**
 * role constant
 *
 * @author 刘家辉
 * @date 2023/11/28
 */
public class RoleConstant {
    public static final Integer ADMIN_ROLE = 2;
    public static final Integer COMMON_ROLE = 1;
    public static HashMap<Integer, String> MAP = new HashMap<>(2);

    static {
        MAP.put(ADMIN_ROLE, "ROLE_ADMIN");
        MAP.put(COMMON_ROLE, "ROLE_USER");
    }
}

