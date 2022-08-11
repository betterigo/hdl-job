package io.github.betterigo.job.client.common.core;

/**
 * <p>Title: TaskInterceptor</p>  
 * <p>Description:任务拦截器 </p>
 * @author haodonglei
 * @since 2020年7月30日
 */
public interface JobInterceptor {
	void beforeExecTask(IJob job);
	void afterExecTask(IJob job, Object result);
}
