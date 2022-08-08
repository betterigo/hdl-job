package io.github.betterigo.job.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan("io.github.betterigo.job.mapper")
@Configuration
public class MybatisConfig {

}
