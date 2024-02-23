package www.raven.jc.dao.mapper;

import org.springframework.data.mongodb.repository.MongoRepository;
import www.raven.jc.entity.po.Message;

/**
 * message mapper
 *
 * @author 刘家辉
 * @date 2024/02/23
 */
public interface MessageMapper extends MongoRepository<Message, String>{
}
