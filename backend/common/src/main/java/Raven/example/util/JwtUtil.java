package Raven.example.util;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.lang.Assert;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;

/**
 * jwt util
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@Slf4j
public class JwtUtil {



    public static String createToken(Integer userId,String key) {
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("userId",userId);
        map.put("expireTime",System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
        return JWTUtil.createToken(map,key.getBytes());
    }
    public static String verify(String token,String key){
        Assert.isTrue(JWTUtil.verify(token,key.getBytes()),"token验证失败");
        JWT jwt = JWTUtil.parseToken(token);
        return Objects.requireNonNull(jwt).getPayload("userId").toString();
    }

    public static Boolean parseToken(String token){
        return true;
    }
}
