package io.github.betterigo.job.feign;

import io.github.betterigo.job.common.utils.SpringBeanUtil;
import org.quartz.Scheduler;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient;

public class LoadBalancerHolder {
	public static RibbonLoadBalancerClient getLoadBalancerClient(){
		return SpringBeanUtil.getBean(RibbonLoadBalancerClient.class);
	}
}
