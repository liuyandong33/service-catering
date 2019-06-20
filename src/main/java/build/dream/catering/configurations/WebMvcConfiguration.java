package build.dream.catering.configurations;

import build.dream.catering.interceptors.CacheHandlerMethodHandlerInterceptor;
import build.dream.catering.interceptors.ThreadNameHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private CacheHandlerMethodHandlerInterceptor cacheHandlerMethodHandlerInterceptor;
    @Autowired
    private ThreadNameHandlerInterceptor threadNameHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cacheHandlerMethodHandlerInterceptor).addPathPatterns("/**");
        registry.addInterceptor(threadNameHandlerInterceptor).addPathPatterns("/**");
    }
}
