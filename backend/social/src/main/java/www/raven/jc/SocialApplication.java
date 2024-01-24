package www.raven.jc;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * social application
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
@EnableAspectJAutoProxy
public class SocialApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocialApplication.class, args);
    }
}