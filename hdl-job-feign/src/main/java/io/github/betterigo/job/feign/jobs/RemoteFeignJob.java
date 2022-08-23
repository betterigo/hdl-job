package io.github.betterigo.job.feign.jobs;

import io.github.betterigo.job.feign.LoadBalancerHolder;
import okhttp3.*;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class RemoteFeignJob implements Job {
	
	private static final Logger logger = LoggerFactory.getLogger(RemoteFeignJob.class);
	
	private OkHttpClient httpClient;
	
	private static final String EXEC_URL = "/hdl/job/remote-exec";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String taskName = (String) context.getMergedJobDataMap().get("taskName");
		if(httpClient == null) {
			initHttpClient();
		}
		String balanceUri = getLoadBalancerUri(context);
		if(StringUtils.isEmpty(balanceUri)) {
			return;
		}
		HttpUrl.Builder urlBuilder = HttpUrl.parse(balanceUri).newBuilder();
		urlBuilder.addQueryParameter("name", taskName);
		Request request = new Request.Builder()
				.url(urlBuilder.build())
				.get()
				.build();
		Call call = httpClient.newCall(request);
		logger.info("remote task execute,name:{},host:{}",taskName,urlBuilder.build().host());
		call.enqueue(new Callback() {
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				logger.info("task-{} exec successfully callback",taskName);
			}
			
			@Override
			public void onFailure(Call call, IOException e) {
				logger.error("task-{} exec failed callback",taskName,e);
			}
		});
	}
	
	private String getLoadBalancerUri(JobExecutionContext context) {
		LoadBalancerClient lb = LoadBalancerHolder.getLoadBalancerClient();
		String clientName =  (String) context.getMergedJobDataMap().get("clientName");
		String taskName = (String) context.getMergedJobDataMap().get("taskName");
		if(lb!=null && clientName!=null) {
			ServiceInstance server = lb.choose(clientName);
			if(server != null) {
				return server.getUri().toString() + EXEC_URL;
			}
		}
		logger.warn("无法找到任务：{}在线的被调用服务：{}",taskName,clientName);
		return null;
		
	}
	private void initHttpClient() {
		httpClient = new OkHttpClient();
	}
}
