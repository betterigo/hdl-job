package io.github.betterigo.job.admin;

import io.github.betterigo.job.admin.config.MybatisConfig;
import io.github.betterigo.job.admin.config.SchedulerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author hdl
 * @since 2022/8/9 13:45
 */
@Configuration
@Import({MybatisConfig.class, SchedulerConfig.class})
@ComponentScans(
        {
            @ComponentScan("io.github.betterigo.job.admin.service"),
            @ComponentScan("io.github.betterigo.job.admin.core")
        }
)
public class HdlJobAdminAutoConfiguration {
}
