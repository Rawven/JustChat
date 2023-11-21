package Raven.example.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

/**
 * register model
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@Data
@Accessors(chain = true)
public class RegisterModel {
    private String username;
    private String password;
    private String email;
}
