package www.raven.jc.dao;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.MessageMapper;
import www.raven.jc.entity.po.Message;

/**
 * mongo service
 *
 * @author 刘家辉
 * @date 2023/12/17
 */
@Repository
public class MessageDAO  {
    public static final String COLLECTION_MESSAGE = "message";
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MessageMapper messageMapper;
    public MessageMapper getBaseMapper() {
        return messageMapper;
    }



    public List<Message> getBatchIds(List<ObjectId> ids) {
        return mongoTemplate.find(new Query(Criteria.where("_id").in(ids)), Message.class, COLLECTION_MESSAGE);
    }

    public Page<Message> getMsgWithPagination(String receiverId,String type, Pageable pageable) {
        Criteria criteria = Criteria.where("receiverId").is(receiverId).and("type").is(type);
        Query query = new Query(criteria);
        // 添加排序条件
        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        List<Message> messages = mongoTemplate.find(query, Message.class, COLLECTION_MESSAGE);
        long count = mongoTemplate.count(query, Message.class, COLLECTION_MESSAGE);
        return new PageImpl<>(messages, pageable, count);
    }
}
