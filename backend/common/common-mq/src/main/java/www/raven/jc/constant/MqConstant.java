package www.raven.jc.constant;

/**
 * mq constant
 *
 * @author 刘家辉
 * @date 2023/12/10
 */
public class MqConstant {
    /**
     * 过期时间默认10分钟
     */
    public final static long EXPIRE_TIME = 10;

    public static final String HEAD = "rocketMq_msg_";
    public static final String HEADER_KEYS = "ROCKET_KEYS";
    public static final String HEADER_TAGS = "ROCKET_TAGS";

    /**
     * User模块
     */
    public static final String TAGS_USER_FRIEND_APPLY = "FRIEND_APPLY";

    /**
     * Chat模块
     */
    public static final String TAGS_CHAT_ROOM_APPLY = "ROOM_APPLY";
    public static final String TAGS_CHAT_ROOM_MSG_RECORD = "ROOM_MSG_RECORD";
    public static final String TAGS_CHAT_FRIEND_MSG_RECORD = "FRIEND_MSG_RECORD";

    /**
     * Moment模块
     */
    public static final String TAGS_MOMENT_INTERNAL_RELEASE_RECORD = "MOMENT_RELEASE_RECORD";
    public static final String TAGS_MOMENT_INTERNAL_COMMENT_RECORD = "MOMENT_COMMENT_RECORD";
    public static final String TAGS_MOMENT_INTERNAL_LIKE_RECORD = "MOMENT_LIKE_RECORD";
    public static final String TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT = "RECORD_MOMENT";
    public static final String TAGS_MOMENT_NOTICE_MOMENT_FRIEND = "RECORD_MOMENT_FRIEND";
}
