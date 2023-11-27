package www.raven.jc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import www.raven.jc.dto.UserRoleInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * user service
 *
 * @author 刘家辉
 * @date 2023/11/27
 */
@Service
@Slf4j
public class UserDetailImpl implements ReactiveUserDetailsService {
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private DiscoveryClient client;


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        ServiceInstance serviceInstance = client.getInstances("JC-Account").get(0);
        String host = serviceInstance.getHost();
        int port = serviceInstance.getPort();
        String serviceUrl = "http://" + host + ":" + port + "/account/get/" + username;
        log.info("serviceUrl is:{}", serviceUrl);
        return webClientBuilder.build()
                .get()
                .uri(serviceUrl)
                .retrieve()
                .bodyToMono(UserRoleInfo.class)
                .map(userRoleInfo ->{
                    List<SimpleGrantedAuthority> authorities = Arrays.stream(userRoleInfo.getRole().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                       return new User(userRoleInfo.getUsername(),
                        userRoleInfo.getPassword(),
                        authorities);});
        }
}
