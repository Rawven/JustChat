package www.raven.jc.util;

import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import www.raven.jc.dto.TokenDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * jwt util
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@Slf4j
public class JwtUtil {


    public static String createToken(Map<String,Object> map, String key) {
        return JWTUtil.createToken(map, key.getBytes());
    }
//    public static String createToken(Integer userId, String key) {
//        HashMap<String, Object> map = new HashMap<>(2);
//        map.put("userId", userId);
//        map.put("expireTime", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
//        return JWTUtil.createToken(map, key.getBytes());
//    }

    public static TokenDTO verify(String token, String key) {
        Assert.isTrue(JWTUtil.verify(token, key.getBytes()), "token验证失败");
        JWTPayload payload = JWTUtil.parseToken(token).getPayload();
        NumberWithFormat expireTime = (NumberWithFormat)payload.getClaim("expireTime");
        long l = expireTime.longValue();
        Assert.isTrue(l > System.currentTimeMillis(), "token已过期");
        String role = (String) payload.getClaim("role");
        NumberWithFormat userId = (NumberWithFormat) payload.getClaim("userId");
        return new TokenDTO().setRole(role).setUserId(userId.intValue());
    }

}
