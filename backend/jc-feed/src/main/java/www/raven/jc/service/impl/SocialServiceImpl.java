package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
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
import www.raven.jc.constant.TimelineFeedConstant;
import www.raven.jc.constant.SocialUserMqConstant;
import www.raven.jc.dao.MomentDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.model.CommentModel;
import www.raven.jc.entity.model.LikeModel;
import www.raven.jc.entity.model.MomentModel;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.entity.po.Moment;
import www.raven.jc.entity.po.Reply;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.event.model.MomentNoticeEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.TimelineFeedService;
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
    @Autowired
    private TimelineFeedService timelineFeedService;

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
            .setTimestamp(System.currentTimeMillis())
            .setComments(new ArrayList<>())
            .setLikes(new ArrayList<>());
        Moment save = momentDAO.save(moment);
        Assert.isTrue(save.getMomentId() != null, "发布失败");
        timelineFeedService.insertMomentFeed(userId, save);
        handleEvent( save.getMomentId().toHexString(), userId, "发布了新的朋友圈",
            SocialUserMqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND);
       }

    @Override
    public void deleteMoment(String momentId) {
        Assert.isTrue(momentDAO.delete(momentId), "删除失败");
        //待定
    }

    @Override
    public void likeMoment(LikeModel likeModel) {
        int userId = RequestUtil.getUserId(request);
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(userId);
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Like like = new Like().setTimestamp(System.currentTimeMillis()).setUserInfo(data);
        Assert.isTrue(momentDAO.like(likeModel.getMomentId(), like), "点赞失败");
        handleEvent(likeModel.getMomentId(), likeModel.getMomentUserId(), "有人点赞了你的朋友圈",
            SocialUserMqConstant.TAGS_MOMENT_INTERNAL_LIKE_RECORD);
    }

    @Override
    public void commentMoment(CommentModel model) {
        int userId = RequestUtil.getUserId(request);
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(userId);
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        if (model.getCommentId() == null) {
            Comment comment = new Comment().setId(IdUtil.getSnowflakeNextIdStr())
                .setTimestamp(System.currentTimeMillis())
                .setUserInfo(data)
                .setContent(model.getText())
                .setReplies(new ArrayList<>());
            Assert.isTrue(momentDAO.comment(model.getMomentId(), comment), "评论失败");
            } else {
            Reply reply = new Reply().setUserInfo(data)
                .setContent(model.getText())
                .setTimestamp(System.currentTimeMillis())
                .setParentId(model.getCommentId());
            Assert.isTrue(momentDAO.reply(model.getMomentId(), model.getCommentId(), reply), "回复失败");
          }
        //发布更新事件
        handleEvent(model.getMomentId(), model.getMomentUserId(), "有人回复了你的评论", SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT);
    }

    @Override
    public List<MomentVO> queryMoment(int userId) {
        RScoredSortedSet<Integer> scoredSortedSet = redissonClient.getScoredSortedSet(TimelineFeedConstant.PREFIX + userId);
        List<Moment> moments;
        //存在时间线
        if (scoredSortedSet.isExists()) {
            // 获取有序集合的所有元素
            List<Integer> collect = scoredSortedSet.stream().collect(Collectors.toList());
             moments = momentDAO.queryBatchMomentsById(collect);
        }else {
            RpcResult<List<UserInfoDTO>> friendInfos = userDubbo.getFriendAndMeInfos(userId);
            Assert.isTrue(friendInfos.isSuccess(), "获取好友信息失败");
            moments = momentDAO.queryBatchMomentsByUserInfo(friendInfos.getData());
            List<MomentVO> collect = moments.stream().map(MomentVO::new).collect(Collectors.toList());
            timelineFeedService.addMomentTimelineFeeding(collect, userId);
        }
        return moments.stream().map(MomentVO::new).collect(Collectors.toList());
    }

    private void handleEvent(String momentId,Integer userId,String msg,String tag) {
        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(momentId).setUserId(userId).setMsg(msg)),
            tag));
    }
}
