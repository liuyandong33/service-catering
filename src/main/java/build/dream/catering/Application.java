package build.dream.catering;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@ServletComponentScan
@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
@MapperScan(basePackages = {"build.dream.common.mappers", "build.dream.catering.mappers"})
public class Application {
    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(Application.class, args);
    }
}
