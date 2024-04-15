package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.MomentMapper;
import www.raven.jc.entity.po.Moment;

/**
 * moment dao
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Repository
public class MomentDAO extends ServiceImpl<MomentMapper, Moment> {

}
