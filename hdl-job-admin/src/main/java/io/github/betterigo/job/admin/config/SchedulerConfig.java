package io.github.betterigo.job.admin.config;

import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class SchedulerConfig {

	@Value("${task.quartz.database.url:''}")
	private String databaseUrl;
	@Value("${task.quartz.database.user:''}")
	private String databaseUser;
	@Value("${task.quartz.database.password:''}")
	private String databasePwd;
	
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/intquartz.properties"));
        //在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        Properties p = propertiesFactoryBean.getObject();
        p.setProperty("org.quartz.dataSource.qzDS.URL", databaseUrl);
        p.setProperty("org.quartz.dataSource.qzDS.user", databaseUser);
        p.setProperty("org.quartz.dataSource.qzDS.password", databasePwd);
        return propertiesFactoryBean.getObject();
    }

    /*
     * quartz初始化监听器
     */
//    @Bean
    public QuartzInitializerListener executorListener() {
        return new QuartzInitializerListener();
    }

    /*
     * 通过SchedulerFactoryBean获取Scheduler的实例
     */
    @Primary
    @Bean(name = "Scheduler")
    public Scheduler scheduler() throws IOException {
        return schedulerFactoryBean().getScheduler();
    }

}
