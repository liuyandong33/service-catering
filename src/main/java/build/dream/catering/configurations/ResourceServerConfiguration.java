package build.dream.catering.configurations;

import build.dream.catering.constants.Constants;
import build.dream.common.annotations.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

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
            "/o2o/obtainVipInfo",
            "/dietOrder/doPayCombined",
            "/sale/**"
    };

    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(Constants.RESOURCE_ID_CATERING);
        resources.tokenStore(tokenStore());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        Map<RequestMappingInfo, HandlerMethod> requestMappingInfoHandlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        Map<Class<?>, List<Map.Entry<RequestMappingInfo, HandlerMethod>>> map = requestMappingInfoHandlerMethodMap.entrySet().stream().collect(Collectors.groupingBy(requestMappingInfoHandlerMethodEntry -> requestMappingInfoHandlerMethodEntry.getValue().getBeanType()));

        List<String> permitAllAntPatterns = new ArrayList<String>();
        permitAllAntPatterns.addAll(Arrays.asList(PERMIT_ALL_ANT_PATTERNS));

        for (Map.Entry<Class<?>, List<Map.Entry<RequestMappingInfo, HandlerMethod>>> entry : map.entrySet()) {
            Class<?> controllerClass = entry.getKey();
            List<Map.Entry<RequestMappingInfo, HandlerMethod>> value = entry.getValue();

            if (AnnotationUtils.findAnnotation(controllerClass, PermitAll.class) != null) {
                for (Map.Entry<RequestMappingInfo, HandlerMethod> item : value) {
                    RequestMappingInfo requestMappingInfo = item.getKey();
                    PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
                    permitAllAntPatterns.addAll(patternsCondition.getPatterns());
                }
            } else {
                for (Map.Entry<RequestMappingInfo, HandlerMethod> item : value) {
                    if (AnnotationUtils.findAnnotation(item.getValue().getMethod(), PermitAll.class) == null) {
                        continue;
                    }

                    RequestMappingInfo requestMappingInfo = item.getKey();
                    PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
                    permitAllAntPatterns.addAll(patternsCondition.getPatterns());
                }
            }
        }

        http.authorizeRequests().antMatchers(permitAllAntPatterns.toArray(new String[0])).permitAll().anyRequest().authenticated();
    }
}
