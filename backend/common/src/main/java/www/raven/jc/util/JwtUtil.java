package www.raven.jc.util;

import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import www.raven.jc.dto.TokenDTO;

import java.util.List;
import java.util.Map;

/**
 * jwt util
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@Slf4j
public class JwtUtil {


    public static String createToken(Map<String, Object> map, String key) {
        map.put("expireTime", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
        return JWTUtil.createToken(map, key.getBytes());
    }

    public static TokenDTO verify(String token, String key) {
        Assert.isTrue(JWTUtil.verify(token, key.getBytes()), "token验证失败");
        JWTPayload payload = JWTUtil.parseToken(token).getPayload();
        NumberWithFormat expireTime = (NumberWithFormat) payload.getClaim("expireTime");
        List<String> role = (List<String>) payload.getClaim("role");
        NumberWithFormat userId = (NumberWithFormat) payload.getClaim("userId");
        return new TokenDTO().setRole(role).setUserId(userId.intValue()).setExpireTime(expireTime.longValue());
    }

}
