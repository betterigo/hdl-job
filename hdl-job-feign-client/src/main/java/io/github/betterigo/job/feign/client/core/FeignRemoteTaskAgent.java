package io.github.betterigo.job.feign.client.core;

import com.google.common.base.Objects;
import io.github.betterigo.job.client.common.core.IJob;
import io.github.betterigo.job.client.common.core.JobInterceptorChain;
import io.github.betterigo.job.client.common.core.JobRegister;
import io.github.betterigo.job.client.common.core.persistant.JobLoader;
import io.github.betterigo.job.client.common.core.persistant.PersistJobHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


@Service
public class FeignRemoteTaskAgent implements RemoteTaskAgent {

	private static final Logger logger = LoggerFactory.getLogger(FeignRemoteTaskAgent.class);
	
	@Autowired
	private JobRegister jobRegister;
	
	//此处使用了拦截器调用链，该类实现了TaskInterceptor接口。
	@Autowired(required = false)
	private JobInterceptorChain interceptor;
	
    @Autowired
    private JobLoader jobLoader;
    
	@Autowired
	private PersistJobHolder persistJobHolder;
	
	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	// 设置核心线程数
	{
		executor.initialize();
		executor.setCorePoolSize(10);
		// 设置最大线程数
		executor.setMaxPoolSize(100);
		// 设置线程活跃时间（秒）
		executor.setKeepAliveSeconds(60);
		// 设置默认线程名称
		executor.setThreadNamePrefix("msg-sender-task-");
		// 设置拒绝策略
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		// 等待所有任务结束后再关闭线程池
		executor.setWaitForTasksToCompleteOnShutdown(true);
	}
	
	@Override
	public void execTask(String jobName) {
		logger.info("exec task --> {}",jobName);
		IJob planningJob = jobRegister.getjob(jobName);
		if(planningJob == null) {
			logger.warn("没有找到本地可执行计划，task-name:{}",jobName);
			//从远程服务端获取任务
			List<IJob> jobs = jobLoader.loadTask();
			//查询是否含有该名字的任务
			if(!CollectionUtils.isEmpty(jobs)) {
				Optional<IJob> taskOpt = jobs.stream().filter(t->Objects.equal(t.getJobName(), jobName)).findFirst();
				if(taskOpt.isPresent()) {
					planningJob = taskOpt.get();
					persistJobHolder.addJob(planningJob.getJobName(), planningJob);
					logger.info("从远程服务器获取计划任务：{}", jobName);
				}else {
					//如果一直获取不到，说明服务端有问题，正在执行中的任务和数据库中的任务不一样！
					logger.error("无法从远程服务器获取计划任务：{}", jobName);
				}
			}
			if(planningJob == null) {
				return;
			}
		}
		if(interceptor!=null) {
			interceptor.beforeExecTask(planningJob);
		}
		CompletableFuture<?> f = null;
//		if(planningTask.getSupplier()!=null) {
//			//有返回值
//			f = CompletableFuture.supplyAsync(planningTask.getSupplier(),executor);
//		}
		final IJob exeJob = planningJob;
		if(planningJob.getJob()!=null) {
			//没有返回值
			f = CompletableFuture.runAsync(
					()->{
						exeJob.getJob().accept(exeJob.getMetaData());
					}
					, executor).exceptionally(fn->{
						logger.error("",fn.getCause());
						return null;
					});
		}
		if(f!=null) {			
			f.thenApply(result->{
				if(interceptor!=null) {
					interceptor.afterExecTask(exeJob,result);
				}
				return result;
			});
		}
	}

}
