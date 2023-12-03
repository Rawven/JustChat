package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.po.ChatRoom;
import www.raven.jc.entity.vo.RealRoomVO;
import www.raven.jc.entity.vo.RoomVO;
import www.raven.jc.feign.UserFeign;
import www.raven.jc.service.RoomService;

import javax.servlet.http.HttpServletRequest;
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
    private HttpServletRequest request;
    @Autowired
    private UserFeign userFeign;

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void createRoom(RoomModel roomModel) {
        ChatRoom room = new ChatRoom().setRoomName(roomModel.getName())
                .setRoomDescription(roomModel.getDescription())
                .setMaxPeople(roomModel.getMaxPeople()).
                setFounderId(Integer.parseInt(request.getHeader("userId")));
        Assert.isTrue(roomDAO.getBaseMapper().insert(room) > 0);
    }

    @Override
    public RealRoomVO queryAllRoomPage(Integer page, Integer size) {
        Long total = roomDAO.getBaseMapper().selectCount(null);
        Page<ChatRoom> chatRoomPage = roomDAO.getBaseMapper().selectPage(new Page<>(page, size), null);
        List<RoomVO> rooms = buildRoomVO(chatRoomPage, userFeign.getAllInfo().getData());
        return new RealRoomVO().setRooms(rooms).setTotal(Math.toIntExact(total));
    }

    @Override
    public RealRoomVO queryLikedRoomList(String column, String text, int page) {
        Long total = roomDAO.getBaseMapper().selectCount(null);
        Page<ChatRoom> chatRoomPage = roomDAO.getBaseMapper().selectPage(new Page<>(page, 5), new QueryWrapper<ChatRoom>().like(column, text));
        List<RoomVO> rooms = buildRoomVO(chatRoomPage, userFeign.getAllInfo().getData());
        return new RealRoomVO().setRooms(rooms).setTotal(Math.toIntExact(total));
    }

    @Override
    public RealRoomVO queryUserNameRoomList(String column, String text, int page) {
        Long total = roomDAO.getBaseMapper().selectCount(null);
        List<UserInfoDTO> queryList = userFeign.getRelatedInfoList(new QueryUserInfoDTO().setColumn(column).setText(text)).getData();
        List<Integer> userIds = queryList.stream().map(UserInfoDTO::getUserId).collect(Collectors.toList());
        Page<ChatRoom> chatRoomPage = roomDAO.getBaseMapper().selectPage(new Page<>(page, 5), new QueryWrapper<ChatRoom>().in("founder_id", userIds));
        List<RoomVO> rooms = buildRoomVO(chatRoomPage, queryList);
        return new RealRoomVO().setRooms(rooms).setTotal(Math.toIntExact(total));
    }


    private List<RoomVO> buildRoomVO(Page<ChatRoom> chatRoomPage, List<UserInfoDTO> data) {
        Assert.isTrue(chatRoomPage.getTotal() > 0);
        Map<Integer, UserInfoDTO> map = data.stream().collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
        return chatRoomPage.getRecords().stream().map(chatRoom -> new RoomVO()
                .setRoomId(chatRoom.getRoomId())
                .setRoomName(chatRoom.getRoomName())
                .setRoomDescription(chatRoom.getRoomDescription())
                .setMaxPeople(chatRoom.getMaxPeople())
                .setFounderName(map.get(chatRoom.getFounderId()).getUsername())).collect(Collectors.toList());
    }


}
