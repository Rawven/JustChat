package www.raven.jc.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.entity.po.Moment;

import java.util.List;

/**
 * moment dao
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Repository
public class MomentDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String COLLECTION_MOMENT = "moment";

    public Moment save(Moment moment) {
        return mongoTemplate.save(moment, COLLECTION_MOMENT);
    }

    public boolean delete(String momentId) {
        return mongoTemplate.remove(momentId).getDeletedCount() > 0;
    }

    public boolean like(String momentId, Like like) {
        return mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(momentId)),
                new Update().push("likes", like), COLLECTION_MOMENT).getModifiedCount() > 0;
    }

    public boolean comment(String momentId, Comment comment) {
        return mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(momentId)),
                new Update().push("comments", comment), COLLECTION_MOMENT).getModifiedCount() > 0;
    }

    public List<Moment> queryMoment(List<UserInfoDTO> infos) {
         //查找 ids 中的用户的动态 按照时间排序 只查7条
        Query with = new Query(Criteria.where("userInfo").in(infos)).limit(7).with(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mongoTemplate.find(with, Moment.class, COLLECTION_MOMENT);

    }
}
