package io.github.betterigo.job.feign.client;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;


@ComponentScans({
        @ComponentScan("io.github.betterigo.job.feign.client.service"),
        @ComponentScan("io.github.betterigo.job.feign.client.core"),
        @ComponentScan("io.github.betterigo.job.feign.client.endpoint")
})
@Configuration
public class HdlJobFeignClientAutoConfiguration {

}
