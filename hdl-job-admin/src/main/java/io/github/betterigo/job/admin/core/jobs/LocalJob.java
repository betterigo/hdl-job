package io.github.betterigo.job.admin.core.jobs;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.betterigo.job.admin.core.ServiceHolder;
import io.github.betterigo.job.admin.core.TaskStatus;
import io.github.betterigo.job.admin.entity.Task;
import io.github.betterigo.job.admin.service.ITaskService;
import io.github.betterigo.job.common.settings.RemoteJobInfo;
import io.github.betterigo.job.common.utils.SpringBeanUtil;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class LocalJob implements Job {
	
	private static Logger logger = LoggerFactory.getLogger(LocalJob.class);
	
    private ObjectMapper mapper = new ObjectMapper();
    
    private static final int TASK_HOLD_TIME_MILLIS = 30 * 1000;
    
    /**
     * 同步服务器与数据库之间的任务
     * TODO 需要添加延迟机制，如果一个任务刚刚添加到了scheduler，那么在10秒内这个任务是受到保护的，不能进行删除操作
     */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("sync database taskinfo...");
		ITaskService service = ServiceHolder.getTaskService();
		//已经停止的计划任务，不应该再次载入
		List<Task> list = service.lambdaQuery()
				.ne(Task::getStatus, TaskStatus.STOPPED.getValue())
//				.or(wrp -> {
//					wrp.gt(Task::getUpdateTime, new Date(System.currentTimeMillis() - TASK_HOLD_TIME_MILLIS))
//					.or().eq(Task::getUpdateTime, null);
//				})
				.list();
		Scheduler scheduler = ServiceHolder.getScheduler();
		try {
			reloadTask(list, scheduler);
		} catch (SchedulerException | ClassNotFoundException e) {
			logger.error("",e);
		}
		
	}
	private void reloadTask(List<Task> list, Scheduler scheduler) throws SchedulerException, ClassNotFoundException {
		if(mapper == null) {
			mapper = new ObjectMapper();
		}
		TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
		Set<JobKey> jobkeys = scheduler.getJobKeys(GroupMatcher.anyGroup());//当前的任务
		//获取要添加的任务
		List<Task> addList = getAddJobs(jobkeys, list);
		//获取加载的远程任务实现类
		RemoteJobInfo remoteJobInfo = SpringBeanUtil.getBean(RemoteJobInfo.class);
		String className = remoteJobInfo.remoteJobClassName();
		//加载远程任务类
		Class<? extends Job> remoteJobClass = (Class<? extends Job>) Class.forName(className);
		for(Task task : addList) {			
			JobDataMap dataMap = new JobDataMap();
			//加入一个字段，表示这是从数据库中load进来的task，不用再次在Listener中再次入库，防止数据异常
			dataMap.putAsString("isLoad", true);
			dataMap.put("clientName", task.getGroupName());
			dataMap.put("cron", task.getCron());
//			dataMap.put("lb", ribbonLoadBalancerClient);
			dataMap.put("taskClass", task.getExecutorClass());
			dataMap.putAsString("times", task.getTotalTimes() == null ? 0 : task.getTotalTimes());
			dataMap.putAsString("period", task.getPeriod() == null ? 0 : task.getPeriod());
			try {
				JSONObject json = JSONObject.parseObject(new String(task.getMetaData().getBytes(1, (int) task.getMetaData().length()), StandardCharsets.UTF_8));
				dataMap.put("metaData", json.toJSONString());
//				dataMap.putAll(metaData);
			} catch (Exception e) {
				logger.error("",e);
			}
			String jobId = task.getName(); 
			dataMap.put("taskName", jobId);
			JobDetail jobDetail = JobBuilder.newJob(remoteJobClass).usingJobData(dataMap).withIdentity(jobId,task.getGroupName()).build();
			TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
	                .withDescription(task.getGroupName() + " trigger")
	                .withIdentity(jobId,task.getGroupName());
		if(!StringUtils.isEmpty(task.getCron())) {			
			triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(task.getCron()));
		}else {
			if(task.getTotalTimes()>0) {	
				int leftTimes = task.getTotalTimes() - task.getExecTimes();
				if(leftTimes>0) {					
					triggerBuilder.withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(leftTimes, task.getPeriod()));
				}else {
					continue;
				}
			}else {
				triggerBuilder.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(task.getPeriod()));
			}
		}
			synchronized (this) {//可能有线程安全问题			
				boolean exist = scheduler.checkExists(jobDetail.getKey());
				if(!exist) {		
					try {
						scheduler.scheduleJob(jobDetail, triggerBuilder.build());
					} catch (Exception e) {
						logger.error("",e);
					}
					if(task.getStatus().equals(TaskStatus.PAUSE.getValue())) {
						scheduler.pauseJob(jobDetail.getKey());
					}
					logger.info("同步数据库-添加定时任务 job:{},trigger-cron:{}",jobId,task.getCron());
				}
			}
		}
		
		//要删除的任务
		List<JobKey> delList = getRemoveJobs(jobkeys, list);
		scheduler.deleteJobs(delList);
		logger.info("同步数据库-清除定时{}个任务",delList.size());
	}
	
	/**
	 * @Description 要删除的任务
	 * @param jobkeys
	 * @param list
	 * @return
	 */
	private List<JobKey> getRemoveJobs(Set<JobKey> jobkeys,List<Task> list){
		List<JobKey> delList = new ArrayList<>();
		for(JobKey jk : jobkeys) {
			boolean inDb = list.stream().anyMatch(t->{
				return Objects.equals(t.getName(), jk.getName()) && Objects.equals(t.getGroupName(), jk.getGroup());
			});
			if(!inDb) {
				if(Objects.equals(jk.getName(), "mysql-task-sync") && Objects.equals(jk.getGroup(), "local-service")) {
					//这是个同步数据的任务，不能清除
				}else {					
					delList.add(jk);
				}
			}
		}
		return delList;
	}
	
	/**
	 * @Description 要添加的任务
	 * @param jobkeys
	 * @param list
	 * @return
	 */
	private List<Task> getAddJobs(Set<JobKey> jobkeys,List<Task> list){
		List<Task> addList = new ArrayList<>();
		for(Task task : list) {
			boolean inScheduler = jobkeys.stream().anyMatch(jk->{
				return Objects.equals(task.getName(), jk.getName()) && Objects.equals(task.getGroupName(), jk.getGroup()); 
			});
			if(!inScheduler) {
				addList.add(task);
			}
		}
		return addList;
	}
}
