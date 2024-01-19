package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.dao.UserRoomDAO;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.po.Room;
import www.raven.jc.entity.po.UserRoom;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.event.RoomMsgEvent;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.ChatService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

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
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private MessageDAO messageDAO;
    @Autowired
    private UserRoomDAO userRoomDAO;
    @Autowired
    private RoomDAO roomDAO;
    @Autowired
    private UserDubbo userDubbo;
    @Autowired
    private StreamBridge streamBridge;


    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void saveMsg(UserInfoDTO data, MessageDTO message, String roomId) {
        long timeStamp = message.getTime();
        String text = message.getText();
        Message realMsg = new Message().setContent(text)
                .setTimestamp(new Date(timeStamp))
                .setSenderId(data.getUserId())
                .setRoomId(Integer.parseInt(roomId));
        //保存消息
        Assert.isTrue(messageDAO.save(realMsg), "插入失败");
        //更新聊天室的最后一条消息
        Assert.isTrue(roomDAO.getBaseMapper().updateById(new Room().setRoomId(Integer.valueOf(roomId)).setLastMsgId(realMsg.getMessageId().toString())) > 0, "更新失败");
        List<UserRoom> ids = userRoomDAO.getBaseMapper().selectList(new QueryWrapper<UserRoom>().eq("room_id", roomId));
        List<Integer> userIds = ids.stream().map(UserRoom::getUserId).collect(Collectors.toList());
        RoomMsgEvent roomMsgEvent = new RoomMsgEvent(data.getUserId(), Integer.valueOf(roomId), userIds, JsonUtil.objToJson(realMsg));
        //通知user模块有新消息
        streamBridge.send("producer-out-0", MqUtil.createMsg(JsonUtil.objToJson(roomMsgEvent), "RECORD"));
    }

    @Override
    public List<MessageVO> restoreHistory(Integer roomId) {
        List<Message> messages = messageDAO.getByRoomId(roomId);
        List<Integer> userIds = messages.stream().map(Message::getSenderId).collect(Collectors.toList());
                CommonResult<List<UserInfoDTO>> allInfo = userDubbo.getBatchInfo(userIds);
        Assert.isTrue(allInfo.getCode() == 200, "userFeign调用失败");
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
