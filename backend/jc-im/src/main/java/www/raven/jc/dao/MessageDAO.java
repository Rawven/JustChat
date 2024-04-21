package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.MessageMapper;
import www.raven.jc.entity.po.Message;

/**
 * mongo service
 *
 * @author 刘家辉
 * @date 2023/12/17
 */
@Repository
public class MessageDAO extends ServiceImpl<MessageMapper, Message> {

}
