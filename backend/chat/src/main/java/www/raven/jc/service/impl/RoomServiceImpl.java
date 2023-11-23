package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.RoomMapper;
import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.po.ChatRoom;
import www.raven.jc.service.RoomService;

import javax.servlet.http.HttpServletRequest;

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
    @Override
    public void createRoom(RoomModel roomModel) {
        ChatRoom room = new ChatRoom().setRoomName(roomModel.getName())
                .setRoomDescription(roomModel.getDescription())
                .setMaxPeople(roomModel.getMaxPeople()).
                setFounderId(Integer.parseInt(request.getHeader("userId")));
       Assert.isTrue(roomMapper.insert(room)>0);
    }
}
