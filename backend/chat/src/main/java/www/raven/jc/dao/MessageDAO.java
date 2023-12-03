package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.mapper.MessageMapper;
import www.raven.jc.entity.po.Message;

/**
 * message
 *
 * @author 刘家辉
 * @date 2023/12/01
 */
@Service
public class MessageDAO extends ServiceImpl<MessageMapper, Message> {
}
