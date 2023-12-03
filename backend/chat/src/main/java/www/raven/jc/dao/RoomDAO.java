package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.mapper.RoomMapper;
import www.raven.jc.entity.po.ChatRoom;

/**
 *
 * @author 刘家辉
 * @date 2023/12/01
 */
@Service
public class RoomDAO extends ServiceImpl<RoomMapper, ChatRoom> {
}
