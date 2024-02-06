package www.raven.jc.util;

import cn.hutool.http.server.HttpServerRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * request util
 *
 * @author 刘家辉
 * @date 2024/02/06
 */
public class RequestUtil {
    public static int getUserId(HttpServletRequest request) {
        return Integer.parseInt(request.getHeader("userId"));
    }
}