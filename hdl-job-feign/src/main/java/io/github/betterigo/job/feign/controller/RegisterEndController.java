package io.github.betterigo.job.feign.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.betterigo.job.client.common.bean.TaskEntity;
import io.github.betterigo.job.common.pojo.JobEntity;
import io.github.betterigo.job.common.service.JobService;
import io.github.betterigo.job.feign.jobs.RemoteFeignJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@RestController
public class RegisterEndController {

	private static Logger logger = LoggerFactory.getLogger(RegisterEndController.class);

	@Autowired
	@Qualifier("Scheduler")
	private Scheduler scheduler;

	@Autowired
	private JobService jobService;

	private static final int TASK_HOLD_TIME_MILLIS = 30 * 1000;

	/**
	 * <p>
	 * Title: register
	 * </p>
	 * <p>
	 * Description: 注册远程任务
	 * </p>
	 * 
	 * @param cron
	 * @param taskName
	 * @param serviceName
	 * @throws SchedulerException 
	 */
	@GetMapping("/task/reg")
	public String register(@RequestParam String cron, @RequestParam String taskName, @RequestParam String serviceName) throws SchedulerException {
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("clientName", serviceName);
		dataMap.put("cron", cron);
//		dataMap.put("lb", ribbonLoadBalancerClient);
		String jobId = taskName; 
//				+ "-scheduler@job-" + UUID.randomUUID().toString();
		dataMap.put("taskName", jobId);
		JobDetail jobDetail = JobBuilder.newJob(RemoteFeignJob.class).usingJobData(dataMap).withIdentity(jobId,serviceName).build();
//		JobDetail j1 = JobBuilder.newJob().
		Trigger trigger = TriggerBuilder.newTrigger()
	                .withDescription(serviceName + " trigger")
//	                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(3,10))
	                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
	                .withIdentity(jobId,serviceName)
	                .build();
		synchronized (this) {//可能有线程安全问题			
//			scheduler.checkExists(jobDetail.getKey());
			boolean exist = scheduler.checkExists(jobDetail.getKey());
			if(!exist) {			
				scheduler.scheduleJob(jobDetail, trigger);
				logger.info("添加定时任务 job:{},trigger-cron:{}",jobId,cron);
			}
		}
		return jobId;
	}
	
	/**
	 * <p>
	 * Title: register
	 * </p>
	 * <p>
	 * Description: 注册远程任务
	 * 现在的实现方案是，先注册到scheduler里面，然后在listener中的监听中持久化到数据库中。
	 * 这种实现方案存在一个问题需要解决，那就是如果数据库中已经有了该任务应该怎么办的问题
	 * </p>
	 *
	 * @throws SchedulerException
	 */
	@PostMapping("/task/reg-ps")
	public String register(@RequestBody TaskEntity taskEntity) throws SchedulerException {
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("clientName", taskEntity.getServiceName());
		dataMap.put("cron", taskEntity.getCron());
		dataMap.putAsString("times", taskEntity.getTimes());
		dataMap.putAsString("period", taskEntity.getPeriod());
//		dataMap.put("lb", ribbonLoadBalancerClient);
		dataMap.put("taskClass", taskEntity.getTaskClassName());
		dataMap.put("metaData", JSON.toJSONString(taskEntity.getMetaData()));
		String jobId = taskEntity.getTaskName();
		dataMap.put("taskName", jobId);
		JobDetail jobDetail = JobBuilder.newJob(RemoteFeignJob.class).usingJobData(dataMap).withIdentity(jobId,taskEntity.getServiceName()).build();
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
	                .withDescription(taskEntity.getServiceName() + " trigger")
	                .withIdentity(jobId,taskEntity.getServiceName());
		if(!StringUtils.isEmpty(taskEntity.getCron())) {			
			triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(taskEntity.getCron()));
		}else {
			if(taskEntity.getTimes()>0) {
				triggerBuilder.withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(taskEntity.getTimes(), taskEntity.getPeriod()));
			}else {
				triggerBuilder.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(taskEntity.getPeriod()));
			}
		}
		synchronized (this) {//可能有线程安全问题			
			boolean exist = scheduler.checkExists(jobDetail.getKey());
			if(!exist) {			
				scheduler.scheduleJob(jobDetail, triggerBuilder.build());
				logger.info("添加定时任务 job:{},trigger-cron:{}",jobId,taskEntity.getCron());
			}
		}
		return jobId;
	}
	
	@GetMapping("/task/unreg")
	public boolean unRegister(@RequestParam String taskName,@RequestParam String serviceName) {
		TriggerKey triggerKey = TriggerKey.triggerKey(taskName, serviceName);
		try {
//			scheduler.pauseTrigger(triggerKey);
//			scheduler.unscheduleJob(triggerKey);
			scheduler.deleteJob(JobKey.jobKey(taskName, serviceName));
			return true;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * <p>Title: pauseTask</p>
	 * <p>Description: 暂停任务</p>   
	 * @param taskName
	 * @param serviceName
	 * @return
	 * @throws SchedulerException 
	 */
	@GetMapping("/task/pause")
	public boolean pauseTask(@RequestParam String taskName,@RequestParam String serviceName) throws SchedulerException {
		scheduler.pauseJob(new JobKey(taskName, serviceName));
		return true;
	}
	
	@GetMapping("/task/resume")
	public boolean resumeTask(@RequestParam String taskName,@RequestParam String serviceName) throws SchedulerException {
		scheduler.resumeJob(new JobKey(taskName, serviceName));
		return true;
	}

	/**
	 * 获取任务列表
	 * @param serviceName 服务名称（分组名称）
	 * @return
	 */
	@GetMapping("/task/entity")
	public List<TaskEntity> getTaskEntityList(@RequestParam String serviceName){
		List<JobEntity> list = jobService.listJobsByGroup(serviceName);
		List<TaskEntity> result = list.stream().map(task -> {
			try {
				JSONObject json = JSONObject.parseObject(new String(task.getMetaData().getBytes(1, (int) task.getMetaData().length()), StandardCharsets.UTF_8));
				TaskEntity taskEntity = new TaskEntity();
				taskEntity.setCron(task.getCron());
				Map<String, String> mtd = new HashMap<>();
				for(Entry<String, Object> entry : json.getInnerMap().entrySet()) {
					mtd.put(entry.getKey(), entry.getValue().toString());
				}
				taskEntity.setMetaData(mtd);
				taskEntity.setTaskClassName(task.getExecutorClass());
				taskEntity.setServiceName(task.getGroupName());
				taskEntity.setTaskName(task.getName());
				return taskEntity;
			}catch (Exception e) {
				logger.error("",e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
		return result;
	}
}
