package io.github.betterigo.job.client.common.core.persistant;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个任务的基本信息
 * @author Lenovo
 *
 */
public class JobInfo {
	
	/**
	 * 间隔模式的间隔时间，当cron有值的时候，该值不生效
	 */
	private int period;
	
	/**
	 * 当使用间隔模式的时候，该字段设置执行次数，默认0
	 */
	private int times;
    /**
     * cron
     */
    private String cron;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 元数据
     */
    private Map<String, String> metaData; //这里可以使用builder构建
    /**
     * bean name suffix
     */
    public static final String BEAN_NAME_META_SUFFIX = "_bean_@name";
	public JobInfo() {
		super();
		this.times = 0;
	}
	public int getPeriod() {
		return period;
	}
	/**
	 * 间隔模式的间隔时间，当cron有值的时候，该值不生效
	 */
	public void setPeriod(int period) {
		this.period = period;
	}
	public int getTimes() {
		return times;
	}
	/**
	 * 当使用间隔模式的时候，该字段设置执行次数，默认1
	 */
	public void setTimes(int times) {
		this.times = times;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public Map<String, String> getMetaData() {
		return metaData;
	}
	public void setMetaData(Map<String, String> metaData) {
		this.metaData = metaData;
	}
	public JobInfo addMetaData(String key, String value) {
		if(this.metaData == null) {
			this.metaData = new HashMap<>();
		}
		this.metaData.put(key, value);
		return this;
	}
}
