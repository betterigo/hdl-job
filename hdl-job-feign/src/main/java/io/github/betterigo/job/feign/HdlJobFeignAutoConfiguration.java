package io.github.betterigo.job.feign;

import io.github.betterigo.job.common.settings.RemoteJobInfo;
import io.github.betterigo.job.feign.config.RemoteFeignJobInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hdl
 * @since 2022/8/9 13:31
 */
@Configuration
public class HdlJobFeignAutoConfiguration {

    @ConditionalOnMissingBean(RemoteJobInfo.class)
    @Bean
    public RemoteJobInfo createRemoteJobInfo(){
        return new RemoteFeignJobInfo();
    }

}
