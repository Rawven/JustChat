package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.mapper.UserRoleMapper;
import www.raven.jc.entity.po.UserRole;

/**
 * user role dao
 *
 * @author 刘家辉
 * @date 2023/11/30
 */
@Service
public class UserRoleDAO extends ServiceImpl<UserRoleMapper, UserRole> {
}
