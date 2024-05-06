package www.raven.jc.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")
    public String endpoint;
    @Value("${minio.accessKey}")
    public String accessKey;
    @Value("${minio.secretKey}")
    public String secretKey;
    @Value("${minio.bucket}")
    public String bucket;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }
}
