package io.github.betterigo.job.admin.core;

import io.github.betterigo.job.common.utils.SpringBeanUtil;
import io.github.betterigo.job.admin.service.ITaskService;
import org.quartz.Scheduler;

public class ServiceHolder {

	public static ITaskService getTaskService() {
		return SpringBeanUtil.getBean(ITaskService.class);
	}
	
	public static Scheduler getScheduler() {
		return (Scheduler) SpringBeanUtil.getBean("Scheduler");
	}
}
