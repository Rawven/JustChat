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
import www.raven.jc.constant.ScoredSortedSetConstant;
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
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.CacheAsyncService;
import www.raven.jc.service.SocialService;
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
    private CacheAsyncService cacheAsyncService;

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
        cacheAsyncService.updateMomentCacheAndNotice(userId, save);
       }

    @Override
    public void deleteMoment(String momentId) {
        Assert.isTrue(momentDAO.delete(momentId));
    }

    @Override
    public void likeMoment(LikeModel likeModel) {
        int userId = RequestUtil.getUserId(request);
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(userId);
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Like like = new Like().setTimestamp(System.currentTimeMillis()).setUserInfo(data);
        Assert.isTrue(momentDAO.like(likeModel.getMomentId(), like), "点赞失败");
        cacheAsyncService.updateLikeCacheAndNotice(userId, likeModel.getMomentTimeStamp(), like);
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
            cacheAsyncService.updateCommentCacheAndNotice(userId, model.getMomentTimeStamp(), comment);
            } else {
            Reply reply = new Reply().setUserInfo(data)
                .setContent(model.getText())
                .setTimestamp(System.currentTimeMillis())
                .setParentId(model.getCommentId());
            Assert.isTrue(momentDAO.reply(model.getMomentId(), model.getCommentId(), reply), "回复失败");
            cacheAsyncService.updateReplyCacheAndNotice(userId, model.getMomentTimeStamp(), reply);
          }
        //发布更新事件
    }

    @Override
    public List<MomentVO> queryMoment(int userId) {
        RScoredSortedSet<MomentVO> scoredSortedSet = redissonClient.getScoredSortedSet(ScoredSortedSetConstant.PREFIX + userId);
        if (scoredSortedSet.isExists()) {
            // 获取有序集合的所有元素
            return scoredSortedSet.stream().collect(Collectors.toList());
        }
        RpcResult<List<UserInfoDTO>> friendInfos = userDubbo.getFriendAndMeInfos(userId);
        Assert.isTrue(friendInfos.isSuccess(), "获取好友信息失败");
        List<Moment> moments = momentDAO.queryMoment(friendInfos.getData());
        List<MomentVO> collect = moments.stream().map(MomentVO::new).collect(Collectors.toList());
        cacheAsyncService.addMomentCache(collect, userId);
        return collect;
    }



}
