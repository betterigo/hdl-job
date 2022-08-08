package io.github.betterigo.job.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.betterigo.job.core.jobs.TestJob;
import io.github.betterigo.job.core.listeners.ExecutedListener;
import org.quartz.*;
import org.quartz.listeners.BroadcastJobListener;
import org.quartz.listeners.BroadcastSchedulerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledBootstrap implements CommandLineRunner {
	
	private static Logger logger = LoggerFactory.getLogger(ScheduledBootstrap.class);
	
	@Autowired
	@Qualifier("Scheduler")
	private Scheduler scheduler;

	@Autowired //使用JobListener会报错，貌似和rabbitMq的依赖注入冲突了
	private ExecutedListener jobListener;

	@Autowired(required = false)
	private List<SchedulerListener> schedulerListeners;

	private ObjectMapper mapper;
	
	@Override
	public void run(String... args) throws Exception {
		if(!CollectionUtils.isEmpty(schedulerListeners)){
			BroadcastSchedulerListener broadcastSchedulerListener = new BroadcastSchedulerListener(schedulerListeners);
			scheduler.getListenerManager().addSchedulerListener(broadcastSchedulerListener);
		}
		if(jobListener!=null){
			BroadcastJobListener broadcastJobListener = new BroadcastJobListener("job-listeners", new ArrayList<JobListener>(){{add(jobListener);}});
			scheduler.getListenerManager().addJobListener(broadcastJobListener);
		}
		scheduler.start();
		JobDetail jobDetail = JobBuilder.newJob(TestJob.class).withIdentity("testJob","group1")
				.build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("testJob", "group1")
				.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(10))
				.startNow().build();
		boolean exists = scheduler.checkExists(jobDetail.getKey());
		if(!exists){
			scheduler.scheduleJob(jobDetail,trigger);
		}else {

		}
	}
}
