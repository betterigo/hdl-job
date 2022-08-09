package io.github.betterigo.job.client.common.bean;

import io.github.betterigo.job.client.common.constant.TaskStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Title: TaskBean</p>  
 * <p>Description:计划详情类 </p>
 * @author haodonglei
 * @since 2020年7月28日
 */
public class TaskBean implements Serializable{
	private static final long serialVersionUID = -6566877181990800742L;
	private Date lastExecTime;
	private Date nextExecTime;
	private String name;
	private String cron;
	private String group;
	private TaskStatus status;
	public Date getLastExecTime() {
		return lastExecTime;
	}
	public void setLastExecTime(Date lastExecTime) {
		this.lastExecTime = lastExecTime;
	}
	public Date getNextExecTime() {
		return nextExecTime;
	}
	public void setNextExecTime(Date nextExecTime) {
		this.nextExecTime = nextExecTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public TaskStatus getStatus() {
		return status;
	}
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
}
