package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.dao.UserRoomDAO;
import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.po.Room;
import www.raven.jc.entity.po.UserRoom;
import www.raven.jc.entity.vo.RealRoomVO;
import www.raven.jc.entity.vo.RoomVO;
import www.raven.jc.event.Event;
import www.raven.jc.event.JoinRoomApplyEvent;
import www.raven.jc.feign.UserFeign;
import www.raven.jc.service.RoomService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * room service impl
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomDAO roomDAO;
    @Autowired
    private UserRoomDAO userRoomDAO;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private StreamBridge streamBridge;

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void createRoom(RoomModel roomModel) {
        Room room = new Room().setRoomName(roomModel.getName())
                .setRoomDescription(roomModel.getDescription())
                .setMaxPeople(roomModel.getMaxPeople()).
                setFounderId(Integer.parseInt(request.getHeader("userId")));
        Assert.isTrue(roomDAO.getBaseMapper().insert(room) > 0);
    }

    @Override
    public RealRoomVO queryAllRoomPage(Integer page, Integer size) {
        Long total = roomDAO.getBaseMapper().selectCount(null);
        Page<Room> chatRoomPage = roomDAO.getBaseMapper().selectPage(new Page<>(page, size), null);
        List<RoomVO> rooms = buildRoomVO(chatRoomPage, userFeign.getAllInfo().getData());
        return new RealRoomVO().setRooms(rooms).setTotal(Math.toIntExact(total));
    }

    @Override
    public RealRoomVO queryLikedRoomList(String column, String text, int page) {
        Long total = roomDAO.getBaseMapper().selectCount(null);
        Page<Room> chatRoomPage = roomDAO.getBaseMapper().selectPage(new Page<>(page, 5), new QueryWrapper<Room>().like(column, text));
        List<RoomVO> rooms = buildRoomVO(chatRoomPage, userFeign.getAllInfo().getData());
        return new RealRoomVO().setRooms(rooms).setTotal(Math.toIntExact(total));
    }

    @Override
    public RealRoomVO queryUserNameRoomList(String column, String text, int page) {
        Long total = roomDAO.getBaseMapper().selectCount(null);
        List<UserInfoDTO> queryList = userFeign.getRelatedInfoList(new QueryUserInfoDTO().setColumn(column).setText(text)).getData();
        List<Integer> userIds = queryList.stream().map(UserInfoDTO::getUserId).collect(Collectors.toList());
        Page<Room> chatRoomPage = roomDAO.getBaseMapper().selectPage(new Page<>(page, 5), new QueryWrapper<Room>().in("founder_id", userIds));
        List<RoomVO> rooms = buildRoomVO(chatRoomPage, queryList);
        return new RealRoomVO().setRooms(rooms).setTotal(Math.toIntExact(total));
    }

    @Override
    public void applyToJoinRoom(Integer roomId) {
        int userId = Integer.parseInt(request.getHeader("userId"));
        Assert.isNull(userRoomDAO.getBaseMapper().selectOne(new QueryWrapper<UserRoom>().eq("user_id", userId).eq("room_id", roomId)),
                "您已经在这个聊天室了");
        Room room = roomDAO.getBaseMapper().selectById(roomId);
        Integer founderId = room.getFounderId();
        streamBridge.send("producer-out-1", MqUtil.createMsg(JsonUtil.objToJson(new JoinRoomApplyEvent(userId, founderId, roomId)), new String[]{"APPLY"}));
    }


    private List<RoomVO> buildRoomVO(Page<Room> chatRoomPage, List<UserInfoDTO> data) {
        Assert.isTrue(chatRoomPage.getTotal() > 0);
        Map<Integer, UserInfoDTO> map = data.stream().collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
        return chatRoomPage.getRecords().stream().map(room -> new RoomVO()
                .setRoomId(room.getRoomId())
                .setRoomName(room.getRoomName())
                .setRoomDescription(room.getRoomDescription())
                .setMaxPeople(room.getMaxPeople())
                .setFounderName(map.get(room.getFounderId()).getUsername())).collect(Collectors.toList());
    }


}
