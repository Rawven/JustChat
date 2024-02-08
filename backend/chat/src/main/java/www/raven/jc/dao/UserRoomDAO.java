package www.raven.jc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.entity.po.UserRoom;

/**
 * user room dao
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@Repository
public class UserRoomDAO extends ServiceImpl<BaseMapper<UserRoom>, UserRoom> {
}
