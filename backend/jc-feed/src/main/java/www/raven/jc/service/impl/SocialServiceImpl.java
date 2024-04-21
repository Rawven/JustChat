package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.constant.SocialUserMqConstant;
import www.raven.jc.constant.TimelineFeedConstant;
import www.raven.jc.dao.CommentDAO;
import www.raven.jc.dao.LikeDAO;
import www.raven.jc.dao.MomentDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.model.CommentModel;
import www.raven.jc.entity.model.LikeModel;
import www.raven.jc.entity.model.MomentModel;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.entity.po.Moment;
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

/**
 * social service impl
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Service
@Slf4j
public class SocialServiceImpl implements SocialService {

    private static final String NOT_REPLY = "0";

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private MomentDAO momentDAO;
    @Autowired
    private LikeDAO likeDAO;
    @Autowired
    private CommentDAO commentDAO;
    @Autowired
    private UserRpcService userRpcService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
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
        Like like = new Like().setId(IdUtil.getSnowflakeNextIdStr()).setMomentId(likeModel.getMomentId()).setTimestamp(System.currentTimeMillis()).setUserId(userId);
        int insert = likeDAO.getBaseMapper().insert(like);
        Assert.isTrue(insert > 0, "点赞失败");
        handleEvent(likeModel.getMomentId(), likeModel.getMomentUserId(), "有人点赞了你的朋友圈",
            SocialUserMqConstant.TAGS_MOMENT_INTERNAL_LIKE_RECORD);
    }

    @Override
    public void commentMoment(CommentModel model) {
        int userId = RequestUtil.getUserId(request);
        Comment comment = new Comment().setId(IdUtil.getSnowflakeNextIdStr())
            .setTimestamp(System.currentTimeMillis())
            .setUserId(userId)
            .setContent(model.getText())
            .setMomentId(model.getMomentId());
        if (!Objects.equals(model.getCommentId(), NOT_REPLY)) {
            comment.setParentId(model.getCommentId());
        }
        int insert = commentDAO.getBaseMapper().insert(comment);
        Assert.isTrue(insert > 0, "评论失败");
        //发布更新事件
        handleEvent(model.getMomentId(), model.getMomentUserId(), "有人回复了你的评论", SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT);
    }

    /**
     * query moment
     * TODO 自适应的timeline扩增（即不只是50个）
     *
     * @param page page
     * @param size size
     * @return {@link List}<{@link MomentVO}>
     */
    @Override
    public List<MomentVO> queryMoment(int page, int size) {
        int userId = RequestUtil.getUserId(request);
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(TimelineFeedConstant.PREFIX + userId);
        RpcResult<List<UserInfoDTO>> friendInfos1 = userRpcService.getFriendAndMeInfos(userId);
        Map<Integer, UserInfoDTO> mapInfo = friendInfos1.getData().stream().collect(Collectors.toMap(UserInfoDTO::getUserId, v -> v));
        List<MomentVO> momentVos = new ArrayList<>();
        //存在时间线
        if (scoredSortedSet.isExists() && scoredSortedSet.size() > page * size) {
            // 获取有序集合的所有元素
            long l = Math.multiplyFull(page, size);
            List<String> pageIds = scoredSortedSet.stream().skip(l - 10).limit(size).toList();
            List<Moment> moments = momentDAO.getBaseMapper().selectBatchIds(pageIds);
            loadMomentAll(moments, momentVos, mapInfo);
        } else {
            RpcResult<List<UserInfoDTO>> friendInfos = userRpcService.getFriendAndMeInfos(userId);
            Assert.isTrue(friendInfos.isSuccess(), "获取好友信息失败");
            List<Integer> userIds = new ArrayList<>(friendInfos.getData().stream().map(UserInfoDTO::getUserId).toList());
            userIds.add(userId);
            // 获取指定的十条数据
            Page<Moment> momentPage = momentDAO.getBaseMapper().selectPage(
                new Page<>(page, size), new QueryWrapper<Moment>().in("user_id", userIds).orderByDesc("timestamp"));
            loadMomentAll(momentPage.getRecords(), momentVos, mapInfo);

            // 对无时间线的用户进行时间线构建
            timelineFeedService.buildMomentTimelineFeeding((long) page * size, userIds, userId);
        }
        return momentVos;
    }

    private void loadMomentAll(List<Moment> moments, List<MomentVO> momentVos,
        Map<Integer, UserInfoDTO> mapInfo) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<MomentVO>> tasks = moments.stream().<Callable<MomentVO>>map(moment -> () -> {
                String id = moment.getId();
                List<Comment> comments = commentDAO.getBaseMapper().selectList(new QueryWrapper<Comment>().eq("moment_id", id));
                List<Like> likes = likeDAO.getBaseMapper().selectList(new QueryWrapper<Like>().eq("moment_id", id));
                List<CommentVO> commentVos = comments.stream().map(comment -> new CommentVO(comment, mapInfo.get(comment.getUserId()))).collect(Collectors.toList());
                List<LikeVO> likeVos = likes.stream().map(like -> new LikeVO(like, mapInfo.get(like.getUserId()))).collect(Collectors.toList());
                return new MomentVO()
                    .setMomentId(id)
                    .setLikes(likeVos)
                    .setComments(commentVos)
                    .setImg(moment.getImg())
                    .setContent(moment.getContent())
                    .setTimestamp(moment.getTimestamp())
                    .setUserInfo(mapInfo.get(moment.getUserId()));
            }).collect(Collectors.toList());
            List<Future<MomentVO>> futures = executor.invokeAll(tasks);
            for (Future<MomentVO> future : futures) {
                momentVos.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            // 处理异常
            log.error("loadMomentAll error", e);
        }
    }

    private void handleEvent(String momentId, Integer userId, String msg,
        String tag) {
        MqUtil.sendMsg(rocketMQTemplate, tag, MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(momentId).setUserId(userId).setMsg(msg))));
    }
}
