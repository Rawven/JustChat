package www.raven.jc.api;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import www.raven.jc.dao.FriendDAO;
import www.raven.jc.dto.*;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.FriendService;
import www.raven.jc.service.NoticeService;
import www.raven.jc.service.UserService;

import java.util.List;

/**
 * user dubbo impl
 *
 * @author 刘家辉
 * @date 2024/01/19
 */
@DubboService(interfaceClass = UserDubbo.class, version = "1.0.0", timeout = 15000)
public class UserDubboImpl implements UserDubbo {
    @Autowired
    private UserService userService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private NoticeService noticeService;

    @Override
    public RpcResult<UserInfoDTO> getSingleInfo(Integer userId) {
        return RpcResult.operateSuccess("查找成功", userService.querySingleInfo(userId));
    }

    @Override
    public RpcResult<List<UserInfoDTO>> getAllInfo() {
        return RpcResult.operateSuccess("查找成功", userService.queryAllInfo());
    }

    @Override
    public RpcResult<List<UserInfoDTO>> getRelatedInfoList(QueryUserInfoDTO userInfoDTO) {
        return RpcResult.operateSuccess("查找成功", userService.queryLikedInfoList(userInfoDTO.getColumn(), userInfoDTO.getText()));
    }

    @Override
    public RpcResult<UserAuthDTO> insert(UserRegisterDTO user) {
        return RpcResult.operateSuccess("插入成功", userService.insert(user));
    }

    @Override
    public RpcResult<UserAuthDTO> getUserToAuth(String username) {
        return RpcResult.operateSuccess("查找成功", userService.querySingleInfoByColumn("username", username));
    }

    @Override
    public RpcResult<List<RoleDTO>> getRolesById(Integer userId) {
        return RpcResult.operateSuccess("查找成功", userService.queryRolesById(userId));
    }

    @Override
    public RpcResult<Boolean> checkUserExit(String username) {
        return RpcResult.operateSuccess("查找成功", userService.checkUserExit(username));
    }

    @Override
    public RpcResult<List<UserInfoDTO>> getBatchInfo(List<Integer> userIds) {
        return RpcResult.operateSuccess("查找成功", userService.queryBatchInfo(userIds));
    }

    @Override
    public RpcResult<Void> saveLogOutTime(Integer userId) {
        userService.saveTime(userId);
        return RpcResult.operateSuccess("登出成功");
    }

    @Override
    public RpcResult<List<UserInfoDTO>> getFriendInfos(int userId) {
        return RpcResult.operateSuccess("查找成功", friendService.getFriendInfos(userId));
    }

    @Override
    public RpcResult<Void> deleteNotice(int noticeId) {
        noticeService.deleteNotification(noticeId);
        return RpcResult.operateSuccess("删除成功");
    }
}
