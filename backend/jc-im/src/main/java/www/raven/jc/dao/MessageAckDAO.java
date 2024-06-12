package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.MessageAckMapper;
import www.raven.jc.entity.po.MessageAck;

@Repository
public class MessageAckDAO extends ServiceImpl<MessageAckMapper, MessageAck> {
}
