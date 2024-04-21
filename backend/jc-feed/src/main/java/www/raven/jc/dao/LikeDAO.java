package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.LikeMapper;
import www.raven.jc.entity.po.Like;

@Repository
public class LikeDAO extends ServiceImpl<LikeMapper, Like> {
}
