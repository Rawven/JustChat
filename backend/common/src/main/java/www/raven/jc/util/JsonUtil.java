package www.raven.jc.util;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * json util
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
    }

    public static Map<Object, Object> jsonToMap(String json) throws Exception {
        return OBJECT_MAPPER.readValue(json, Map.class);
    }

    public static String mapToJson(Map<Object, Object> map) throws Exception {
        return OBJECT_MAPPER.writeValueAsString(map);
    }

    public static <T> T jsonToObj(String json, Class<T> clazz) throws Exception {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

}
