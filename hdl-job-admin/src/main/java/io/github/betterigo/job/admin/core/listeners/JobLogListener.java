package io.github.betterigo.job.admin.core.listeners;

import io.github.betterigo.job.admin.entity.TaskLog;
import io.github.betterigo.job.admin.service.ITaskLogService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobLogListener implements SchedulerListener {

	@Autowired
	private ITaskLogService taskLogger;
	
	public static final String OP_CREATE = "create";
	
	public static final String OP_SCHEDULED = "scheduled";
	
	public static final String OP_PAUSE = "pause";
	
	public static final String OP_STOP = "stop";
	
	public static final String OP_RESUME = "resume";
	
	public static final String EXEC_SUCCESS = "success";
	
	private void createTaskLog(Trigger trigger,String op,String result) {
		TaskLog taskLog = new TaskLog();
		taskLog.setGroupName(trigger.getKey().getGroup());
		taskLog.setTaskName(trigger.getKey().getName());
		taskLog.setOperation(op);
		taskLog.setExecResult(result);
		taskLogger.save(taskLog);
	}
	
	private void createTaskLog(JobKey jobKey,String op,String result) {
		TaskLog taskLog = new TaskLog();
		taskLog.setGroupName(jobKey.getGroup());
		taskLog.setTaskName(jobKey.getName());
		taskLog.setOperation(op);
		taskLog.setExecResult(result);
		taskLogger.save(taskLog);
	}
	
	@Override
	public void jobScheduled(Trigger trigger) {
		createTaskLog(trigger, OP_SCHEDULED, EXEC_SUCCESS);
	}


	@Override
	public void jobUnscheduled(TriggerKey triggerKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggerFinalized(Trigger trigger) {
		// TODO Auto-generated method stub
		System.out.println(trigger.getJobKey().getName());

	}

	@Override
	public void triggerPaused(TriggerKey triggerKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggersPaused(String triggerGroup) {
		// TODO Auto-generated method stub

	}

	/**
	 * 触发器的操作会关联该触发器关联的所有job，目前是一个触发器一个job，所以不用在这里进行操作
	 */
	@Override
	public void triggerResumed(TriggerKey triggerKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggersResumed(String triggerGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobAdded(JobDetail jobDetail) {
		createTaskLog(jobDetail.getKey(), OP_CREATE, EXEC_SUCCESS);
	}

	@Override
	public void jobDeleted(JobKey jobKey) {
		createTaskLog(jobKey, OP_STOP, EXEC_SUCCESS);

	}

	@Override
	public void jobPaused(JobKey jobKey) {
		createTaskLog(jobKey, OP_PAUSE, EXEC_SUCCESS);
	}

	@Override
	public void jobsPaused(String jobGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobResumed(JobKey jobKey) {
		createTaskLog(jobKey, OP_RESUME, EXEC_SUCCESS);

	}

	@Override
	public void jobsResumed(String jobGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		TaskLog taskLog = new TaskLog();
		taskLog.setExecResult("error");
		taskLog.setErrorMsg(msg+"#"+cause.getMessage());
		taskLogger.save(taskLog);
	}

	@Override
	public void schedulerInStandbyMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerStarted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerStarting() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerShutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerShuttingdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulingDataCleared() {
		// TODO Auto-generated method stub

	}

}
