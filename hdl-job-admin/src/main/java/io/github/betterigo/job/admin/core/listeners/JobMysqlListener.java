/*
 * Copyright (c) 2020. All Rights Reserved.
 * ProjectName:  intelctrl
 * ClassName: JobMysqlListener
 * Author: hdl
 * Date: 2020/10/30 下午2:12
 */

package io.github.betterigo.job.admin.core.listeners;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.betterigo.job.admin.core.TaskStatus;
import io.github.betterigo.job.admin.core.jobs.LocalJob;
import io.github.betterigo.job.admin.entity.Task;
import io.github.betterigo.job.admin.service.ITaskService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author hdl
 * @description task的Listener，用于在task发生变化时，与数据交互
 * @since 2020/10/30
 */
@Component
public class JobMysqlListener implements SchedulerListener {

    private static final Logger log = LoggerFactory.getLogger(JobMysqlListener.class);

    @Autowired
    private ITaskService iTaskService;
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    @Qualifier("Scheduler")
	private Scheduler scheduler;
    
//	@Autowired
//	private RibbonLoadBalancerClient ribbonLoadBalancerClient;

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link JobDetail}</code>
     * is scheduled.
     * </p>
     *
     * @param trigger
     */
    @Override
    public void jobScheduled(Trigger trigger) {
        String taskName = trigger.getJobKey().getName();
        String group = trigger.getKey().getGroup();
        iTaskService.lambdaUpdate()
                .eq(Task::getName,taskName)
                .eq(Task::getGroupName,group)
                .set(Task::getStatus, TaskStatus.SCHEDULING.getValue())
                .update();
        log.info("{}已经执行",taskName);
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link JobDetail}</code>
     * is unscheduled.
     * </p>
     *
     * @param triggerKey
     * @see SchedulerListener#schedulingDataCleared()
     */
    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
    	 log.info("{}已经执行","jobUnscheduled");
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link Trigger}</code>
     * has reached the condition in which it will never fire again.
     * </p>
     *
     * @param trigger
     */
    @Override
    public void triggerFinalized(Trigger trigger) {
    	 log.info("{}已经执行","triggerFinalized");
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link Trigger}</code>
     * has been paused.
     * </p>
     *
     * @param triggerKey
     */
    @Override
    public void triggerPaused(TriggerKey triggerKey) {
    	   String taskName = triggerKey.getName();
           String group = triggerKey.getGroup();
           iTaskService.lambdaUpdate()
                   .eq(Task::getName,taskName)
                   .eq(Task::getGroupName,group)
                   .set(Task::getStatus, TaskStatus.PAUSE.getValue())
                   .update();
           log.info("{}已经暂停",taskName);
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a
     * group of <code>{@link Trigger}s</code> has been paused.
     * </p>
     *
     * <p>If all groups were paused then triggerGroup will be null</p>
     *
     * @param triggerGroup the paused group, or null if all were paused
     */
    @Override
    public void triggersPaused(String triggerGroup) {
    	 log.info("{}已经执行","triggersPaused");
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link Trigger}</code>
     * has been un-paused.
     * </p>
     *
     * @param triggerKey
     */
    @Override
    public void triggerResumed(TriggerKey triggerKey) {
    	 String taskName = triggerKey.getName();
         String group = triggerKey.getGroup();
         iTaskService.lambdaUpdate()
                 .eq(Task::getName,taskName)
                 .eq(Task::getGroupName,group)
                 .set(Task::getStatus, TaskStatus.SCHEDULING.getValue())
                 .update();
         log.info("{}已经恢复执行",taskName);
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a
     * group of <code>{@link Trigger}s</code> has been un-paused.
     * </p>
     *
     * @param triggerGroup
     */
    @Override
    public void triggersResumed(String triggerGroup) {
    	log.info("{}已经执行","triggersResumed");
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link JobDetail}</code>
     * has been added.
     * </p>
     *
     * @param jobDetail
     */
    @Override
    public void jobAdded(JobDetail jobDetail) {
    	boolean isLoad = false;
    	//如果是数据库同步任务mysql-task-sync。则跳过
    	if(Objects.equals("mysql-task-sync", jobDetail.getKey().getName())) {
    		return;
    	}
    	try {			
    		isLoad = jobDetail.getJobDataMap().getBooleanValue("isLoad");
    		if(isLoad) {
    			//如果是从数据库加载进来的任务，则不作处理
    			return;
    		}
		} catch (Exception e) {
			log.error("",e);
		}
    	QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
    	queryWrapper
    		.eq("name", jobDetail.getKey().getName())
			.eq("group_name", jobDetail.getKey().getGroup());
    	//要保证只有一条数据
    	UpdateWrapper<Task> taskUpdateWrapper = new UpdateWrapper<>();
    	taskUpdateWrapper.eq("name", jobDetail.getKey().getName())
    		.eq("group_name", jobDetail.getKey().getGroup());
    	
//    	iTaskService.remove(queryWrapper);
//        jobDetail.
        //保存任务到数据库
        Task task = new Task();
        task.setCreateTime(LocalDateTime.now());
        task.setCron(jobDetail.getJobDataMap().getString("cron"));
		task.setExecutorClass(jobDetail.getJobDataMap().getString("taskClass"));
        task.setGroupName(jobDetail.getKey().getGroup());
//        System.out.println("计划任务name:"+jobDetail.getKey().getName()+";cron="+task.getCron()+" period="+jobDetail.getJobDataMap().get("period"));
        task.setPeriod(jobDetail.getJobDataMap().getInt("period"));
        task.setTotalTimes(jobDetail.getJobDataMap().getInt("times"));
        task.setExecTimes(0);
        Object meta = jobDetail.getJobDataMap().get("metaData");
        JSONObject json = JSONObject.parseObject(meta.toString());
        task.setMetaData(json.toJSONString().getBytes(StandardCharsets.UTF_8));
        task.setName(jobDetail.getKey().getName());
        task.setStatus(TaskStatus.NOT_SCHEDULED.getValue());//0-新建，1-运行，2-暂停，3-停止
        List<Task> tasks = iTaskService.list(queryWrapper);
        if(CollectionUtils.isEmpty(tasks)) {
        	iTaskService.save(task);
        }else {
        	iTaskService.update(task, taskUpdateWrapper);
        }
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link JobDetail}</code>
     * has been deleted.
     * </p>
     *
     * @param jobKey
     */
    @Override
    public void jobDeleted(JobKey jobKey) {
    	  String taskName = jobKey.getName();
          String group = jobKey.getGroup();
          iTaskService.lambdaUpdate()
                  .eq(Task::getName,taskName)
                  .eq(Task::getGroupName,group)
                  .set(Task::getStatus, TaskStatus.STOPPED.getValue())
                  .set(Task::getUpdateTime, new Date())
                  .set(Task::getStopTime, new Date())
                  .update();
          log.info("{}已经停止暂停",taskName);
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link JobDetail}</code>
     * has been paused.
     * </p>
     *
     * @param jobKey
     */
    @Override
    public void jobPaused(JobKey jobKey) {
    	  String taskName = jobKey.getName();
          String group = jobKey.getGroup();
          iTaskService.lambdaUpdate()
                  .eq(Task::getName,taskName)
                  .eq(Task::getGroupName,group)
                  .set(Task::getStatus, TaskStatus.PAUSE.getValue())
                  .set(Task::getUpdateTime, new Date())
                  .update();
          log.info("{}已经暂停",taskName);
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a
     * group of <code>{@link JobDetail}s</code> has been paused.
     * </p>
     *
     * @param jobGroup the paused group, or null if all were paused
     */
    @Override
    public void jobsPaused(String jobGroup) {
    	log.info("{}已经执行","jobsPaused");
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link JobDetail}</code>
     * has been un-paused.
     * </p>
     *
     * @param jobKey
     */
    @Override
    public void jobResumed(JobKey jobKey) {
    	 String taskName = jobKey.getName();
         String group = jobKey.getGroup();
         iTaskService.lambdaUpdate()
                 .eq(Task::getName,taskName)
                 .eq(Task::getGroupName,group)
                 .set(Task::getStatus, TaskStatus.SCHEDULING.getValue())
                 .set(Task::getUpdateTime, new Date())
                 .update();
         log.info("{}已经恢复执行",taskName);
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a
     * group of <code>{@link JobDetail}s</code> has been un-paused.
     * </p>
     *
     * @param jobGroup
     */
    @Override
    public void jobsResumed(String jobGroup) {
    	log.info("{}已经执行","jobsResumed");
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a serious error has
     * occurred within the scheduler - such as repeated failures in the <code>JobStore</code>,
     * or the inability to instantiate a <code>Job</code> instance when its
     * <code>Trigger</code> has fired.
     * </p>
     *
     * <p>
     * The <code>getErrorCode()</code> method of the given SchedulerException
     * can be used to determine more specific information about the type of
     * error that was encountered.
     * </p>
     *
     * @param msg
     * @param cause
     */
    @Override
    public void schedulerError(String msg, SchedulerException cause) {
    	log.info("{}已经执行","schedulerError");
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> to inform the listener
     * that it has move to standby mode.
     * </p>
     */
    @Override
    public void schedulerInStandbyMode() {
    	log.info("{}已经执行","schedulerInStandbyMode");
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> to inform the listener
     * that it has started.
     * </p>
     */
    @Override
    public void schedulerStarted() {
    	// TODO 需要添加一个任务，用来同步与数据库的交互
    	log.info("{}已经执行","schedulerStarted");
    	log.info("mysql data sync task start");
    	String jobId = "mysql-task-sync";
    	String group = "local-service";
    	TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withDescription("mysql-sync-trigger")
                .withIdentity(jobId,group)
                .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"));
    	JobDataMap dataMap = new JobDataMap();
//    	dataMap.put("iService", iTaskService);
    	dataMap.put("jobId", jobId);
    	dataMap.put("group", group);
//    	dataMap.put("lb", ribbonLoadBalancerClient);
//    	dataMap.put("scheduler", scheduler);
		JobDetail jobDetail = JobBuilder.newJob(LocalJob.class).usingJobData(dataMap).withIdentity(jobId,group).build();
		try {
			scheduler.scheduleJob(jobDetail, triggerBuilder.build());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> to inform the listener
     * that it is starting.
     * </p>
     */
    @Override
    public void schedulerStarting() {
    	log.info("{}已经执行","schedulerStarting");
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> to inform the listener
     * that it has shutdown.
     * </p>
     */
    @Override
    public void schedulerShutdown() {
    	log.info("{}已经执行","schedulerShutdown");
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> to inform the listener
     * that it has begun the shutdown sequence.
     * </p>
     */
    @Override
    public void schedulerShuttingdown() {
    	log.info("{}已经执行","schedulerShutdown");
    }

    /**
     * Called by the <code>{@link Scheduler}</code> to inform the listener
     * that all jobs, triggers and calendars were deleted.
     */
    @Override
    public void schedulingDataCleared() {
    	log.info("{}已经执行","schedulingDataCleared");
    }

}
