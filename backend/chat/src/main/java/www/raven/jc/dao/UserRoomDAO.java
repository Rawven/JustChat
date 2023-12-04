package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.mapper.MessageMapper;
import www.raven.jc.dao.mapper.UserRoomMapper;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.po.UserRoom;

/**
 * user room dao
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@Repository
public class UserRoomDAO extends ServiceImpl<UserRoomMapper, UserRoom> {
}
