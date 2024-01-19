package www.raven.jc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * feign config
 *
 * @author 刘家辉
 * @date 2024/01/20
 */
@Configuration
public class FeignConfig {
    @Bean
    System.Logger.Level level() {
        return System.Logger.Level.INFO;
    }
}
