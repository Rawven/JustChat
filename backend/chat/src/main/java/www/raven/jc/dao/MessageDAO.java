package www.raven.jc.dao;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import www.raven.jc.entity.po.Message;

import java.util.List;

/**
 * mongo service
 *
 * @author 刘家辉
 * @date 2023/12/17
 */
@Repository
public class

MessageDAO {
    public static final String COLLECTION_MESSAGE = "message";
    @Autowired
    private MongoTemplate mongoTemplate;

    public  Boolean save(Message message) {
        mongoTemplate.save(message, COLLECTION_MESSAGE);
        return true;
    }

    public List<Message> getByRoomId(Integer roomId) {
        Criteria criteria = Criteria.where("receiverId").is(roomId).and("type").is("room");
        new Query(criteria).limit(15).with(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mongoTemplate.find(new Query(criteria), Message.class, COLLECTION_MESSAGE);
    }
    public List<Message> getBatchIds(List<ObjectId> ids){
        return mongoTemplate.find(new Query(Criteria.where("_id").in(ids)),Message.class,COLLECTION_MESSAGE);
    }

}
