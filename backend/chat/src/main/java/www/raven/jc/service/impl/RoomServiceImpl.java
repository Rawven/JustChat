package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.RoomMapper;
import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.po.ChatRoom;
import www.raven.jc.entity.vo.RoomVO;
import www.raven.jc.feign.UserInfoFeign;
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
    private RoomMapper roomMapper;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserInfoFeign accountFeign;

    @Override
    public void createRoom(RoomModel roomModel) {
        ChatRoom room = new ChatRoom().setRoomName(roomModel.getName())
                .setRoomDescription(roomModel.getDescription())
                .setMaxPeople(roomModel.getMaxPeople()).
                setFounderId(Integer.parseInt(request.getHeader("userId")));
        Assert.isTrue(roomMapper.insert(room) > 0);
    }

    @Override
    public List<RoomVO> queryAllRoomPage(Integer page) {
        Page<ChatRoom> chatRoomPage = roomMapper.selectPage(new Page<>(page, 5), null);
        return buildRoomVO(chatRoomPage, accountFeign.getAllInfo().getData());
    }

    @Override
    public List<RoomVO> queryLikedRoomList(String column, String text, int page) {
        Page<ChatRoom> chatRoomPage = roomMapper.selectPage(new Page<>(page, 5), new QueryWrapper<ChatRoom>().like(column, text));
        return buildRoomVO(chatRoomPage, accountFeign.getAllInfo().getData());
    }

    @Override
    public List<RoomVO> queryUserNameRoomList(String column, String text, int page) {
        List<UserInfoDTO> queryList = accountFeign.getRelatedInfoList(new QueryUserInfoDTO().setColumn(column).setText(text)).getData();
        List<Integer> userIds = queryList.stream().map(UserInfoDTO::getUserId).collect(Collectors.toList());
        Page<ChatRoom> chatRoomPage = roomMapper.selectPage(new Page<>(page, 5), new QueryWrapper<ChatRoom>().in("founder_id", userIds));
        return buildRoomVO(chatRoomPage, queryList);
    }

    private List<RoomVO> buildRoomVO(Page<ChatRoom> chatRoomPage, List<UserInfoDTO> data) {
        Assert.isTrue(chatRoomPage.getTotal() > 0);
        Map<Integer, UserInfoDTO> map = data.stream().collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
        return chatRoomPage.getRecords().stream().map(chatRoom -> new RoomVO()
                .setRoomId(chatRoom.getRoomId())
                .setRoomName(chatRoom.getRoomName())
                .setRoomDescription(chatRoom.getRoomDescription())
                .setMaxPeople(chatRoom.getMaxPeople())
                .setFounderName(map.get(chatRoom.getFounderId()).getUsername())
                .setFounderAvatar(map.get(chatRoom.getFounderId()).getProfile())).collect(Collectors.toList());
    }


}
