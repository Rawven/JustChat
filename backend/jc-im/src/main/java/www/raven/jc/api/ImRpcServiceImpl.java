package www.raven.jc.api;

import cn.hutool.core.lang.Assert;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.result.RpcResult;

/**
 * im dubbo impl
 *
 * @author 刘家辉
 * @date 2024/02/29
 */

@DubboService(interfaceClass = UserRpcService.class, version = "1.0.0", timeout = 15000)
public class ImRpcServiceImpl implements ImRpcService {
    @Autowired
    private NoticeDAO noticeDAO;

    @Override
    public RpcResult<Void> deleteNotification(int id) {
        int i = noticeDAO.getBaseMapper().deleteById(id);
        Assert.isTrue(i == 1, "删除失败");
        return RpcResult.operateSuccess("删除成功");
    }
}
