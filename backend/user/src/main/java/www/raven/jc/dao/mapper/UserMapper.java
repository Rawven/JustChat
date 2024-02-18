package www.raven.jc.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.springframework.data.repository.query.Param;
import www.raven.jc.entity.po.User;

/**
 * user mapper
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
public interface UserMapper extends BaseMapper<User> {
    /**
     * select user by friend id 联查
     *
     * @param friendId friend id
     * @return {@link List}<{@link User}>
     */
    List<User> selectUserByFriendId (@Param("friendId") int friendId);

    /**
     * select users and friends
     *
     * @param userId user id
     * @return {@link List}<{@link User}>
     */
    List<User> selectUsersAndFriends(@Param("userId") int userId);

}
