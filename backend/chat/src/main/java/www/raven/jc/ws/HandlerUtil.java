package www.raven.jc.ws;

import java.util.HashMap;
import java.util.Map;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.util.JsonUtil;

/**
 * handler util
 *
 * @author 刘家辉
 * @date 2024/01/22
 */
public class HandlerUtil {

    public static String combineMessage(MessageDTO messageDTO, UserInfoDTO data) {
        Map<Object, Object> map = new HashMap<>(2);
        map.put("userInfo", data);
        map.put("message", messageDTO);
        return JsonUtil.mapToJson(map);
    }
}
