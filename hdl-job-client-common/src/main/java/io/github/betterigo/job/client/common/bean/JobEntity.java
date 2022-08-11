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
public class JobEntity {
    /**
     * 计划任务执行器类
     */
    private String jobClassName;
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
    private String jobName;
    
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

	public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
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
