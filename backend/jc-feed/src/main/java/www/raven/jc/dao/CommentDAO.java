package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.CommentMapper;
import www.raven.jc.entity.po.Comment;

@Repository
public class CommentDAO extends ServiceImpl<CommentMapper, Comment> {
}
