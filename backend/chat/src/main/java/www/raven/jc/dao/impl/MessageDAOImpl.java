package www.raven.jc.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.mapper.MessageMapper;
import www.raven.jc.entity.po.Message;

/**
 * message daoimpl
 *
 * @author 刘家辉
 * @date 2023/12/01
 */
@Service
public class MessageDAOImpl extends ServiceImpl<MessageMapper, Message> {
}
