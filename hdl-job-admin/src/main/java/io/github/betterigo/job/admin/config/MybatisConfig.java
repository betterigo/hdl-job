package io.github.betterigo.job.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan("io.github.betterigo.job.admin.mapper")
@Configuration
public class MybatisConfig {

}
