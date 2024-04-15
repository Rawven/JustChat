package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.constant.SocialUserMqConstant;
import www.raven.jc.constant.TimelineFeedConstant;
import www.raven.jc.dao.CommentDAO;
import www.raven.jc.dao.LikeDAO;
import www.raven.jc.dao.MomentCommentDAO;
import www.raven.jc.dao.MomentDAO;
import www.raven.jc.dao.MomentLikeDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.model.CommentModel;
import www.raven.jc.entity.model.LikeModel;
import www.raven.jc.entity.model.MomentModel;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.entity.po.Moment;
import www.raven.jc.entity.po.MomentComment;
import www.raven.jc.entity.po.MomentLike;
import www.raven.jc.entity.vo.CommentVO;
import www.raven.jc.entity.vo.LikeVO;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.event.model.MomentNoticeEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.SocialService;
import www.raven.jc.service.TimelineFeedService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private LikeDAO likeDAO;
    @Autowired
    private CommentDAO commentDAO;
    @Autowired
    private MomentCommentDAO momentCommentDAO;
    @Autowired
    private MomentLikeDAO momentLikeDAO;
    @Autowired
    private UserRpcService userRpcService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private TimelineFeedService timelineFeedService;

    @Override
    public void releaseMoment(MomentModel model) {
        int userId = RequestUtil.getUserId(request);
        Moment moment = new Moment()
                .setUserId(userId)
                .setImg(model.getImg())
                .setContent(model.getText())
                .setTimestamp(System.currentTimeMillis());
        int insert = momentDAO.getBaseMapper().insert(moment);
        Assert.isTrue(insert > 0, "发布失败");
        timelineFeedService.insertMomentFeed(userId, moment);
        handleEvent(moment.getId(), userId, "发布了新的朋友圈",
                SocialUserMqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND);
    }

    @Override
    public void deleteMoment(String momentId) {
        int id = momentDAO.getBaseMapper().delete(new QueryWrapper<Moment>().eq("id", momentId));
        Assert.isTrue(id > 0, "删除失败");
    }

    @Override
    public void likeMoment(LikeModel likeModel) {
        int userId = RequestUtil.getUserId(request);
        RpcResult<UserInfoDTO> singleInfo = userRpcService.getSingleInfo(userId);
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Like like = new Like().setId(IdUtil.getSnowflakeNextIdStr()).setTimestamp(System.currentTimeMillis()).setUserId(userId);
        int insert = likeDAO.getBaseMapper().insert(like);
        Assert.isTrue(insert > 0, "点赞失败");
        int insert1 = momentLikeDAO.getBaseMapper().insert(new MomentLike().setLikeId(like.getId()).setMomentId(likeModel.getMomentId()));
        Assert.isTrue(insert1 > 0, "点赞失败");
        handleEvent(likeModel.getMomentId(), likeModel.getMomentUserId(), "有人点赞了你的朋友圈",
                SocialUserMqConstant.TAGS_MOMENT_INTERNAL_LIKE_RECORD);
    }

    @Override
    public void commentMoment(CommentModel model) {
        int userId = RequestUtil.getUserId(request);
        Comment comment = new Comment().setId(IdUtil.getSnowflakeNextIdStr())
                .setTimestamp(System.currentTimeMillis())
                .setUserId(userId)
                .setContent(model.getText());
        if (model.getCommentId() != null) {
            comment.setParentId(model.getCommentId());
        }
        int insert = commentDAO.getBaseMapper().insert(comment);
        Assert.isTrue(insert > 0, "评论失败");
        int insert1 = momentCommentDAO.getBaseMapper().insert(new MomentComment().setCommentId(comment.getId()).setMomentId(model.getMomentId()));
        Assert.isTrue(insert1 > 0, "评论失败");
        //发布更新事件
        handleEvent(model.getMomentId(), model.getMomentUserId(), "有人回复了你的评论", SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT);
    }

    @Override
    public List<MomentVO> queryMoment(int userId) {
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(TimelineFeedConstant.PREFIX + userId);

        RpcResult<List<UserInfoDTO>> friendInfos1 = userRpcService.getFriendInfos(userId);
        Map<Integer, UserInfoDTO> mapInfo = friendInfos1.getData().stream().collect(Collectors.toMap(UserInfoDTO::getUserId, v -> v));
        //存在时间线
        if (scoredSortedSet.isExists()) {
            // 获取有序集合的所有元素

            List<MomentVO> momentVos = new ArrayList<>();
            List<String> ids = scoredSortedSet.stream().collect(Collectors.toList());
            List<Moment> moments = momentDAO.getBaseMapper().selectBatchIds(ids);
            moments.forEach(moment -> {
                String id = moment.getId();
                List<MomentComment> momentCommentIds = momentCommentDAO.getBaseMapper().selectList(new QueryWrapper<MomentComment>().eq("moment_id", id));
                List<Comment> comments = commentDAO.getBaseMapper().selectBatchIds(momentCommentIds.stream().map(MomentComment::getCommentId).collect(Collectors.toList()));
                List<MomentLike> momentLikes = momentLikeDAO.getBaseMapper().selectList(new QueryWrapper<MomentLike>().eq("moment_id", id));
                List<Like> likes = likeDAO.getBaseMapper().selectBatchIds(momentLikes.stream().map(MomentLike::getLikeId).collect(Collectors.toList()));
                List<CommentVO> commentVos = new ArrayList<>();
                List<LikeVO> likeVos = new ArrayList<>();
                for (Comment comment : comments) {
                    commentVos.add(new CommentVO(comment, mapInfo.get(comment.getUserId())));
                }
                for (Like like : likes) {
                    likeVos.add(new LikeVO(like, mapInfo.get(like.getUserId())));
                }
                momentVos.add(new MomentVO().setMomentId(id).setLikes(likeVos).setComments(commentVos).setImg(moment.getImg()).setContent(moment.getContent())
                        .setTimestamp(moment.getTimestamp()).setUserInfo(mapInfo.get(moment.getUserId())));
            });




        } else {
            RpcResult<List<UserInfoDTO>> friendInfos = userRpcService.getFriendAndMeInfos(userId);
            Assert.isTrue(friendInfos.isSuccess(), "获取好友信息失败");
            moments = momentDAO.queryBatchMomentsByUserInfo(friendInfos.getData());
            List<MomentVO> collect = moments.stream().map(MomentVO::new).collect(Collectors.toList());
            timelineFeedService.addMomentTimelineFeeding(collect, userId);
        }
        return moments.stream().map(MomentVO::new).collect(Collectors.toList());
    }

    private void handleEvent(String momentId, Integer userId, String msg, String tag) {
        streamBridge.send("producer-out-1", MqUtil.createMsg(
                JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(momentId).setUserId(userId).setMsg(msg)),
                tag));
    }
}
