package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
import www.raven.jc.event.MomentReleaseEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.SocialService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * social serivce impl
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Service
public class SocialServiceImpl implements SocialService {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private MomentDAO momentDAO;
    @DubboReference(interfaceClass = UserDubbo.class, version = "1.0.0", timeout = 15000)
    private UserDubbo userDubbo;
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private RedissonClient redissonClient;
    public static final String PREFIX = "moment_";
    @Override
    public void releaseMoment(MomentModel model) {
        String userId = request.getHeader("userId");
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(Integer.valueOf(userId));
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Moment moment = new Moment()
                .setUserInfo(data)
                .setImg(model.getImg())
                .setContent(model.getText())
                .setTimestamp(System.currentTimeMillis());
        Moment save = momentDAO.save(moment);
        Assert.isTrue(save.getMomentId() != null, "发布失败");
        MomentReleaseEvent momentReleaseEvent = new MomentReleaseEvent().setReleaseId(Integer.valueOf(userId)).setMoment(JsonUtil.objToJson(save));
        streamBridge.send("producer-out-0", MqUtil.createMsg(JsonUtil.objToJson(momentReleaseEvent), MqConstant.TAGS_MOMENT_RELEASE_RECORD));
    }
    @Override
    public void deleteMoment(String momentId) {
        Assert.isTrue(momentDAO.delete(momentId));
    }

    @Override
    public void likeMoment(String momentId) {
        String userId = request.getHeader("userId");
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(Integer.valueOf(userId));
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Like like = new Like().setTimestamp(System.currentTimeMillis()).setUserInfo(data)
                .setTimestamp(System.currentTimeMillis());
        Assert.isTrue(momentDAO.like(momentId, like), "点赞失败");

    }

    @Override
    public void commentMoment(CommentModel model) {
        String userId = request.getHeader("userId");
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(Integer.valueOf(userId));
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Comment comment = new Comment().setTimestamp(System.currentTimeMillis())
                .setUserInfo(data)
                .setContent(model.getText());
        Assert.isTrue(momentDAO.comment(model.getMomentId(), comment), "评论失败");
    }

    @Override
    public List<MomentVO> queryMoment(int userId) {
        if (redissonClient.getScoredSortedSet(PREFIX + userId).isExists()) {
            // 获取有序集合
            RScoredSortedSet<MomentVO> scoredSortedSet = redissonClient.getScoredSortedSet(PREFIX + userId);
            // 获取最近3天时间里的10条朋友圈
            long currentTime = System.currentTimeMillis();
            return new ArrayList<>(
                    scoredSortedSet.valueRangeReversed(currentTime - 1000 * 60 * 60 * 24 * 3, true, currentTime, true, 0, 10)
            );
        }
        RpcResult<List<UserInfoDTO>> friendInfos = userDubbo.getFriendAndMeInfos(userId);
        Assert.isTrue(friendInfos.isSuccess(), "获取好友信息失败");
        List<Moment> moments = momentDAO.queryMoment(friendInfos.getData());
        List<MomentVO> collect = moments.stream().map(moment -> {
            MomentVO vo = new MomentVO();
            vo.setMomentId(moment.getMomentId().toHexString());
            vo.setUserInfo(moment.getUserInfo());
            vo.setContent(moment.getContent());
            vo.setImg(moment.getImg());
            vo.setTimestamp(moment.getTimestamp());
            vo.setComments(moment.getComments());
            vo.setLikes(moment.getLikes());
            return vo;
        }).collect(Collectors.toList());
        RScoredSortedSet<MomentVO> scoredSortedSet = redissonClient.getScoredSortedSet(PREFIX+ userId);
        collect.forEach(momentVO -> {
            scoredSortedSet.add(momentVO.getTimestamp(), momentVO);
        });
        return collect;
    }
}
