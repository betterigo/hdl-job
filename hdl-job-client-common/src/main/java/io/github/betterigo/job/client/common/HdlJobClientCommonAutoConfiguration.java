package io.github.betterigo.job.client.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

/**
 * @author hdl
 * @since 2022/8/23 14:03
 */
@ComponentScans({
        @ComponentScan("io.github.betterigo.job.client.common.core")
})
@Configuration
public class HdlJobClientCommonAutoConfiguration {
}
