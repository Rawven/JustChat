package www.raven.jc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * security property
 *
 * @author 刘家辉
 * @date 2024/02/16
 */
@Configuration
public class SecurityProperty {
    @Value("${security.paths.permitAll}")
    public String[] auth;

    @Value("${security.paths.hasAnyRoleUserAdmin}")
    public String[] users;

    @Value("${security.paths.hasRoleAdmin}")
    public String[] admins;

    @Value("${security.roles.user}")
    public String roleUser;

    @Value("${security.roles.admin}")
    public String roleAdmin;
}
