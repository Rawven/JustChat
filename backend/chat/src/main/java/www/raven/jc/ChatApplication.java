package www.raven.jc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * chat application
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@MapperScan("www.raven.jc.dao")
@EnableFeignClients(basePackages = {"www.raven.jc.feign"})
public class ChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
}
