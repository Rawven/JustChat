package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.mapper.UserMapper;
import www.raven.jc.entity.po.User;

/**
 * user dao
 *
 * @author 刘家辉
 * @date 2023/12/03
 */
@Service
public class UserDAO extends ServiceImpl<UserMapper, User> {
}
