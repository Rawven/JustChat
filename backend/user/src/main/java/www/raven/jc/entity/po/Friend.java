package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * friend entity
 *
 * @author 刘家辉
 * @date 2023/12/16
 */
@Data
@Accessors(chain = true)
@TableName("friend")
public class Friend {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long friendId;
    private String status;
    //TODO 看看如何适配
    private String lastMsgId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}