package www.raven.jc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import www.raven.jc.client.IpfsClient;

/**
 * ipfs config
 *
 * @author 刘家辉
 * @date 2023/11/28
 */
@Configuration
public class IpfsConfig {
    @Value("${Ipfs.api}")
    private String api;
    @Bean
    public IpfsClient ipfsClient() {
        return new IpfsClient(api);
    }
}
