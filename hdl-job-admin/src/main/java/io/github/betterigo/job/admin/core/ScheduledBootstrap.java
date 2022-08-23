package io.github.betterigo.job.admin.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.betterigo.job.admin.core.listeners.ExecutedListener;
import io.github.betterigo.job.admin.core.jobs.LocalJob;
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
	
	private static final Logger log = LoggerFactory.getLogger(ScheduledBootstrap.class);
	
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
//		JobDetail jobDetail = JobBuilder.newJob(TestJob.class).withIdentity("testJob","group1")
//				.build();
//		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("testJob", "group1")
//				.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(10))
//				.startNow().build();
//		boolean exists = scheduler.checkExists(jobDetail.getKey());
//		if(!exists){
//			scheduler.scheduleJob(jobDetail,trigger);
//		}else {
//
//		}
		initLocalJob();
	}

	private void initLocalJob() throws SchedulerException {
		log.info("{}已经执行","schedulerStarted");
		log.info("mysql data sync task start");
		String jobId = "mysql-task-sync";
		String group = "local-service";
		TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
				.withDescription("mysql-sync-trigger")
				.withIdentity(jobId, group)
//				.withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"));
				.withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(1));
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("jobId", jobId);
		dataMap.put("group", group);
		JobDetail jobDetail = JobBuilder.newJob(LocalJob.class).usingJobData(dataMap).withIdentity(jobId,group).build();
		boolean exists = scheduler.checkExists(jobDetail.getKey());
		if(exists){
			scheduler.deleteJob(jobDetail.getKey());
		}
		scheduler.scheduleJob(jobDetail, triggerBuilder.build());
	}
}
