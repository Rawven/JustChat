package www.raven.jc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * main
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@MapperScan("www.raven.jc.dao.mapper")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}