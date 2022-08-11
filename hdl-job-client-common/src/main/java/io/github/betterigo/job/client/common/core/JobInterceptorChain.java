package io.github.betterigo.job.client.common.core;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * <p>Title: TaskInterceptorChain</p>  
 * <p>Description:拦截器调用链 </p>
 * @author haodonglei
 * @since 2020年7月30日
 */
public class JobInterceptorChain implements JobInterceptor {
	
	protected List<JobInterceptor> interceptors;

	private List<JobInterceptor> reverseInterceptors;
	
	@Override
	public void beforeExecTask(IJob task) {
		interceptors.stream().forEach(be->{
			be.beforeExecTask(task);
		});
		
	}

	@Override
	public void afterExecTask(IJob task, Object result) {
		//这里应该倒序执行
		if(reverseInterceptors == null) {
			setReverseInterceptors();
		}
		reverseInterceptors.stream().forEach(ae->{
			ae.afterExecTask(task,result);
		});
	}

	private void setReverseInterceptors() {
		reverseInterceptors = Lists.reverse(interceptors);
	}
	
	protected void setInterceptors(List<JobInterceptor> interceptors) {
		this.interceptors = interceptors;
	}
}
