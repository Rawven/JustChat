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
//    private static final String COLLECTION_MOMENT = "moment";
//    @Autowired
//    private MongoTemplate mongoTemplate;
//    @Autowired
//    private MomentMapper momentMapper;
//
//    public MomentMapper getBaseMapper() {
//        return momentMapper;
//    }
//
//    public Moment save(Moment moment) {
//        return mongoTemplate.save(moment, COLLECTION_MOMENT);
//    }
//
//    public boolean delete(String momentId) {
//        return mongoTemplate.remove(momentId).getDeletedCount() > 0;
//    }
//
//    public boolean like(String momentId, Like like) {
//        return mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(momentId)),
//                new Update().push("likes", like), COLLECTION_MOMENT).getModifiedCount() > 0;
//    }
//
//    public boolean comment(String momentId, Comment comment) {
//        return mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(momentId)),
//                new Update().push("comments", comment), COLLECTION_MOMENT).getModifiedCount() > 0;
//    }
//
//    public boolean reply(String momentId, String commentId, Reply reply) {
//        Query query = new Query(Criteria.where("_id").is(momentId).and("comments._id").is(commentId));
//        Update update = new Update().push("comments.$.replies", reply);
//        return mongoTemplate.updateFirst(query, update, COLLECTION_MOMENT).getModifiedCount() > 0;
//    }
//
//    public List<Moment> queryBatchMomentsById(List<Integer> list) {
//        //通过_id查找用户的动态 按照时间排序
//        Query with = new Query(Criteria.where("_id").in(list)).with(Sort.by(Sort.Direction.DESC, "timestamp"));
//        return mongoTemplate.find(with, Moment.class, COLLECTION_MOMENT);
//    }
//
//    public List<Moment> queryBatchMomentsByUserInfo(List<UserInfoDTO> list) {
//        //查找 ids 中的用户的动态 按照时间排序 只查7条
//        Query with = new Query(Criteria.where("userInfo").in(list)).limit(15).with(Sort.by(Sort.Direction.DESC, "timestamp"));
//        return mongoTemplate.find(with, Moment.class, COLLECTION_MOMENT);
//    }

}
