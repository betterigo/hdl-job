/*
 * Copyright (c) 2020. All Rights Reserved.
 * ProjectName:  intelctrl
 * ClassName: TaskEntity
 * Author: hdl
 * Date: 2020/10/29 下午4:29
 */

package io.github.betterigo.job.client.common.bean;

import java.util.Map;

/**
 * @author hdl
 * @description 用于启动服务后，初始化该服务的计划任务
 * @since 2020/10/29
 */
public class TaskEntity {
    /**
     * 计划任务执行器类
     */
    private String taskClassName;
    /**
     * 元数据
     */
    private Map<String, String> metaData;
    /**
     * cron
     */
    private String cron;
    /**
     * 计划任务名称
     */
    private String taskName;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
	/**
	 * 间隔模式的间隔时间，当cron有值的时候，该值不生效
	 */
	private int period;
	
	/**
	 * 当使用间隔模式的时候，该字段设置执行次数，默认0
	 */
	private int times;
    
    public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getTaskClassName() {
        return taskClassName;
    }

    public void setTaskClassName(String taskClassName) {
        this.taskClassName = taskClassName;
    }

    public Map<String, String> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, String> metaData) {
		this.metaData = metaData;
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

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}
}
