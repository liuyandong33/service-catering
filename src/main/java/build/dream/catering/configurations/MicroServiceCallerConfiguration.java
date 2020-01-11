package build.dream.catering.configurations;

import build.dream.common.fallbacks.MicroServiceCallerFallback;
import build.dream.common.utils.MicroServiceCaller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicroServiceCallerConfiguration {
    @Bean
    public MicroServiceCaller microServiceCaller() {
        MicroServiceCaller microServiceCaller = new MicroServiceCaller();
        microServiceCaller.setMicroServiceCallerFallback(new MicroServiceCallerFallback());
        return microServiceCaller;
    }
}
