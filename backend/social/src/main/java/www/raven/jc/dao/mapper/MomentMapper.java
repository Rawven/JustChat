package www.raven.jc.dao.mapper;

import org.springframework.data.mongodb.repository.MongoRepository;
import www.raven.jc.entity.po.Moment;

/**
 * moment mapper
 *
 * @author 刘家辉
 * @date 2024/02/21
 */
public interface MomentMapper extends MongoRepository<Moment, String> {
}
