package io.github.betterigo.job.client.common.core;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>Title: RemoteTask</p>  
 * <p>Description: 远程任务</p>
 * @author haodonglei
 * @since 2020年7月27日
 */
public class PlanningJob implements IJob {
	private String cron;
	private Runnable task;
	private String taskName;
	private Map<String, String> metaData;
	private Supplier<?> supplier;
	
	public PlanningJob(String cron, Runnable task, String taskName) {
		super();
		this.cron = cron;
		this.task = task;
		this.taskName = taskName;
	}
	public PlanningJob(String cron, Supplier<?> supplier, String taskName) {
		super();
		this.cron = cron;
		this.supplier = supplier;
		this.taskName = taskName;
	}
	

	public PlanningJob(String cron, Runnable task, String taskName, Map<String, String> metaData) {
		this(cron, task, taskName);
		this.metaData = metaData;
	}
	
	public PlanningJob(String cron, Supplier<?> supplier, String taskName, Map<String, String> metaData) {
		this(cron, supplier, taskName);
		this.metaData = metaData;
	}


	public String getJobName() {
		return taskName;
	}

	public Map<String, String> getMetaData() {
		return metaData;
	}

	public String getCron() {
		return cron;
	}

	@Override
	public Consumer<Map<String, String>> getJob() {
		return null;
	}

	public Runnable getTaskRunnabel() {
		return task;
	}
	public Supplier<?> getSupplier() {
		return supplier;
	}
}
