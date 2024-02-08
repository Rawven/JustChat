package www.raven.jc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.entity.po.UserRole;

/**
 * user role dao
 *
 * @author 刘家辉
 * @date 2023/11/30
 */
@Repository
public class UserRoleDAO extends ServiceImpl<BaseMapper<UserRole>, UserRole> {
}
