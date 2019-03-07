package build.dream.catering.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    private static final String[] PERMIT_ALL_ANT_PATTERNS = {
            "/favicon.ico",
            "/user/obtainBranchInfo",
            "/demo/**",
            "/weiXin/authCallback",
            "/images/**",
            "/libraries/**",
            "/branch/initializeBranch",
            "/branch/pullBranchInfos",
            "/branch/disableGoods",
            "/branch/renewCallback",
            "/branch/obtainHeadquartersInfo",
            "/meiTuan/test",
            "/eleme/bindingStore",
            "/eleme/doBindingStore",
            "/eleme/tenantAuthorizeCallback",
            "/o2o/obtainVipInfo"
    };

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(PERMIT_ALL_ANT_PATTERNS).permitAll().anyRequest().authenticated();
    }
}
