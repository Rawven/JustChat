package www.raven.jc.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.mapper.UserMapper;
import www.raven.jc.entity.po.User;

/**
 * user daoimpl
 *
 * @author 刘家辉
 * @date 2023/11/30
 */
@Service
public class UserDAOImpl extends ServiceImpl<UserMapper, User> {
}
