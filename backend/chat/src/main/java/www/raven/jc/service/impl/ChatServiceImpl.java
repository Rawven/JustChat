package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.MessageMapper;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.feign.AccountFeign;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.ChatService;
import www.raven.jc.util.JsonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * chat service impl
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private MessageMapper mapper;
    @Autowired
    private AccountFeign accountFeign;

    @Override
    public void saveMsg(UserInfoDTO data, String message) throws Exception {
        Map<Object, Object> msg = JsonUtil.jsonToMap(message);
        long timeStamp = (long) msg.get("time");
        String text = (String) msg.get("text");
        Message realMsg = new Message().setContent(text)
                .setTimestamp(new Date(timeStamp))
                .setSenderId(data.getUserId());
        Assert.isTrue(mapper.insert(realMsg) > 0, "插入失败");
    }

    @Override
    public List<MessageVO> restoreHistory() {
        List<Message> messages = mapper.selectList(null);
        CommonResult<List<UserInfoDTO>> allInfo = accountFeign.getAllInfo();
        List<UserInfoDTO> data = allInfo.getData();
        Map<Integer, UserInfoDTO> userInfoMap = data.stream()
                .collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
        return messages.stream().map(
                message -> {
                    MessageVO messageVO = new MessageVO();
                    messageVO.setText(message.getContent());
                    messageVO.setTime(message.getTimestamp());
                    // 从 Map 中查找对应的 UserInfoDTO 对象
                    UserInfoDTO userInfoDTO = userInfoMap.get(message.getSenderId());
                    if (userInfoDTO != null) {
                        messageVO.setUser(userInfoDTO.getUsername());
                        messageVO.setProfile(userInfoDTO.getProfile());
                    }
                    return messageVO;
                }
        ).collect(Collectors.toList());
    }
}
