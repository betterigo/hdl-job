package io.github.betterigo.job.core;

import io.github.betterigo.job.service.ITaskService;
import io.github.betterigo.job.utils.SpringBeanUtil;
import org.quartz.Scheduler;

public class ServiceHolder {

	public static ITaskService getTaskService() {
		return SpringBeanUtil.getBean(ITaskService.class);
	}
	
	public static Scheduler getScheduler() {
		return (Scheduler) SpringBeanUtil.getBean("Scheduler");
	}
}
