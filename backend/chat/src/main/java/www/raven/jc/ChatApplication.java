package www.raven.jc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * chat application
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
}
