package io.github.betterigo.job.feign.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.betterigo.job.client.common.bean.JobEntity;
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
@RequestMapping("/feign")
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
	@GetMapping("/job/reg")
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
	@PostMapping("/job/reg-ps")
	public String register(@RequestBody JobEntity jobEntity) throws SchedulerException {
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("clientName", jobEntity.getServiceName());
		dataMap.put("cron", jobEntity.getCron());
		dataMap.putAsString("times", jobEntity.getTimes());
		dataMap.putAsString("period", jobEntity.getPeriod());
//		dataMap.put("lb", ribbonLoadBalancerClient);
		dataMap.put("taskClass", jobEntity.getJobClassName());
		dataMap.put("metaData", JSON.toJSONString(jobEntity.getMetaData()));
		String jobId = jobEntity.getJobName();
		dataMap.put("taskName", jobId);
		JobDetail jobDetail = JobBuilder.newJob(RemoteFeignJob.class).usingJobData(dataMap).withIdentity(jobId, jobEntity.getServiceName()).build();
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
	                .withDescription(jobEntity.getServiceName() + " trigger")
	                .withIdentity(jobId, jobEntity.getServiceName());
		if(!StringUtils.isEmpty(jobEntity.getCron())) {
			triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(jobEntity.getCron()));
		}else {
			if(jobEntity.getTimes()>0) {
				triggerBuilder.withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(jobEntity.getTimes(), jobEntity.getPeriod()));
			}else {
				triggerBuilder.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(jobEntity.getPeriod()));
			}
		}
		synchronized (this) {//可能有线程安全问题			
			boolean exist = scheduler.checkExists(jobDetail.getKey());
			if(!exist) {			
				scheduler.scheduleJob(jobDetail, triggerBuilder.build());
				logger.info("添加定时任务 job:{},trigger-cron:{}",jobId, jobEntity.getCron());
			}
		}
		return jobId;
	}
	
	@GetMapping("/job/unreg")
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
	@GetMapping("/job/pause")
	public boolean pauseTask(@RequestParam String taskName,@RequestParam String serviceName) throws SchedulerException {
		scheduler.pauseJob(new JobKey(taskName, serviceName));
		return true;
	}
	
	@GetMapping("/job/resume")
	public boolean resumeTask(@RequestParam String taskName,@RequestParam String serviceName) throws SchedulerException {
		scheduler.resumeJob(new JobKey(taskName, serviceName));
		return true;
	}

	/**
	 * 获取任务列表
	 * @param serviceName 服务名称（分组名称）
	 * @return
	 */
	@GetMapping("/job/entity")
	public List<JobEntity> getTaskEntityList(@RequestParam String serviceName){
		List<io.github.betterigo.job.common.pojo.JobEntity> list = jobService.listJobsByGroup(serviceName);
		List<JobEntity> result = list.stream().map(task -> {
			try {
				JSONObject json = JSONObject.parseObject(new String(task.getMetaData().getBytes(1, (int) task.getMetaData().length()), StandardCharsets.UTF_8));
				JobEntity jobEntity = new JobEntity();
				jobEntity.setCron(task.getCron());
				Map<String, String> mtd = new HashMap<>();
				for(Entry<String, Object> entry : json.getInnerMap().entrySet()) {
					mtd.put(entry.getKey(), entry.getValue().toString());
				}
				jobEntity.setMetaData(mtd);
				jobEntity.setJobClassName(task.getExecutorClass());
				jobEntity.setServiceName(task.getGroupName());
				jobEntity.setJobName(task.getName());
				return jobEntity;
			}catch (Exception e) {
				logger.error("",e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
		return result;
	}
}
