package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.constant.MqConstant;
import www.raven.jc.dao.MomentDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.model.CommentModel;
import www.raven.jc.entity.model.MomentModel;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.entity.po.Moment;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.event.model.MomentCommentEvent;
import www.raven.jc.event.model.MomentLikeEvent;
import www.raven.jc.event.model.MomentReleaseEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.SocialService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.util.RequestUtil;

/**
 * social service impl
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Service
@Slf4j
public class SocialServiceImpl implements SocialService {
    public static final String PREFIX = "moment_";
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private MomentDAO momentDAO;
    @Autowired
    private UserDubbo userDubbo;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StreamBridge streamBridge;

    @Override
    public void releaseMoment(MomentModel model) {
        int userId = RequestUtil.getUserId(request);
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(userId);
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Moment moment = new Moment()
            .setUserInfo(data)
            .setImg(model.getImg())
            .setContent(model.getText())
            .setTimestamp(System.currentTimeMillis());
        Moment save = momentDAO.save(moment);
        Assert.isTrue(save.getMomentId() != null, "发布失败");
        //发布更新事件
        streamBridge.send("producer-out-0", MqUtil.createMsg(JsonUtil.objToJson(new MomentReleaseEvent().setReleaseId(userId).setMoment(save)), MqConstant.TAGS_MOMENT_INTERNAL_RELEASE_RECORD));
    }

    @Override
    public void deleteMoment(String momentId) {
        Assert.isTrue(momentDAO.delete(momentId));
    }

    @Override
    public void likeMoment(String momentId,Integer momentUserId) {
        int userId = RequestUtil.getUserId(request);
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(userId);
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Like like = new Like().setTimestamp(System.currentTimeMillis()).setUserInfo(data)
            .setTimestamp(System.currentTimeMillis());
        Assert.isTrue(momentDAO.like(momentId, like), "点赞失败");
        //发布更新事件
        streamBridge.send("producer-out-0", MqUtil.createMsg(JsonUtil.objToJson(new MomentLikeEvent().setMomentId(momentId).setMomentUserId(momentUserId).setLike(like)), MqConstant.TAGS_MOMENT_INTERNAL_LIKE_RECORD));
    }

    @Override
    public void commentMoment(CommentModel model) {
        int userId = RequestUtil.getUserId(request);
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(userId);
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Comment comment = new Comment().setTimestamp(System.currentTimeMillis())
            .setUserInfo(data)
            .setContent(model.getText());
        Assert.isTrue(momentDAO.comment(model.getMomentId(), comment), "评论失败");
        //发布更新事件
        streamBridge.send("producer-out-0", MqUtil.createMsg(JsonUtil.objToJson(new MomentCommentEvent().setMomentId(model.getMomentId()).setMomentUserId(model.getMomentUserId()).setComment(comment)), MqConstant.TAGS_MOMENT_INTERNAL_COMMENT_RECORD));
    }

    @Override
    public List<MomentVO> queryMoment(int userId) {
        if (redissonClient.getScoredSortedSet(PREFIX + userId).isExists()) {
            // 获取有序集合
            RScoredSortedSet<MomentVO> scoredSortedSet = redissonClient.getScoredSortedSet(PREFIX + userId);
            // 获取最近时间里的10条朋友圈
            return new ArrayList<>(
                scoredSortedSet.valueRangeReversed(0, false, Double.POSITIVE_INFINITY, true, 0, 10)
            );
        }
        RpcResult<List<UserInfoDTO>> friendInfos = userDubbo.getFriendAndMeInfos(userId);
        Assert.isTrue(friendInfos.isSuccess(), "获取好友信息失败");
        List<Moment> moments = momentDAO.queryMoment(friendInfos.getData());
        List<MomentVO> collect = moments.stream().map(MomentVO::new).collect(Collectors.toList());
        RScoredSortedSet<MomentVO> scoredSortedSet = redissonClient.getScoredSortedSet(PREFIX + userId);
        collect.forEach(momentVO -> scoredSortedSet.add(momentVO.getTimestamp(), momentVO));
        return collect;
    }
}
