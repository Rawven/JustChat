package www.raven.jc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * auth application
 *
 * @author 刘家辉
 * @date 2023/11/27
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"www.raven.jc.feign"})
@EnableAspectJAutoProxy
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
