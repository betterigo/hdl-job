package io.github.betterigo.job.admin.core.listeners;

import io.github.betterigo.job.admin.core.TaskStatus;
import io.github.betterigo.job.admin.entity.Task;
import io.github.betterigo.job.admin.entity.TaskLog;
import io.github.betterigo.job.admin.service.ITaskLogService;
import io.github.betterigo.job.admin.service.ITaskService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class ExecutedListener implements JobListener {
	
    @Autowired
    private ITaskService iTaskService;
    
	@Autowired
	private ITaskLogService taskLogger;
	
	public static final String OP_CREATE = "create";
	
	public static final String OP_SCHEDULED = "executed";
	
	public static final String OP_PAUSE = "pause";
	
	public static final String OP_STOP = "stop";
	
	public static final String LOCAL_JOB = "mysql-task-sync";
	
	public static final String EXEC_SUCCESS = "success";
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		JobKey key = context.getJobDetail().getKey();
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String cron = dataMap.getString("cron");
		int times = 0;
		try {
			times = dataMap.getInt("times");
		} catch (Exception e) {
		}
		if(StringUtils.isEmpty(cron) || times != 0) {
			List<Task> tasks = iTaskService.lambdaQuery().eq(Task::getName, key.getName())
			.eq(Task::getGroupName, key.getGroup()).list();
			if(!CollectionUtils.isEmpty(tasks)) {
				Task task = tasks.get(0);
				task.setExecTimes(task.getExecTimes() + 1);
				if(task.getTotalTimes()!=null && task.getTotalTimes()!=0 && task.getTotalTimes() <= task.getExecTimes()) {
					task.setStatus(TaskStatus.STOPPED.getValue());
				}
				task.setUpdateTime(LocalDateTime.now());
				iTaskService.updateById(task);
			}
		}
		if(jobException == null) {
			if(!Objects.equals(LOCAL_JOB, key.getName())) {				
				createTaskLog(key, OP_SCHEDULED, EXEC_SUCCESS);
			}
		}else {
			TaskLog taskLog = new TaskLog();
			taskLog.setExecResult("error");
			taskLog.setGroupName(key.getGroup());
			taskLog.setTaskName(key.getName());
			taskLog.setErrorMsg(jobException.getMessage());
			taskLog.setExecuteTime(LocalDateTime.now());
			taskLogger.save(taskLog);
		}
	}
	
	private void createTaskLog(JobKey jobKey,String op,String result) {
		TaskLog taskLog = new TaskLog();
		taskLog.setGroupName(jobKey.getGroup());
		taskLog.setTaskName(jobKey.getName());
		taskLog.setOperation(op);
		taskLog.setExecResult(result);
		taskLog.setExecuteTime(LocalDateTime.now());
		taskLogger.save(taskLog);
	}
}
