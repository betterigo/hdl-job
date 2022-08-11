package io.github.betterigo.job.client.common.core;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: AbstractTaskInteceptorChainAdapter</p>  
 * <p>Description: 拦截器配置适配器</p>
 * @author haodonglei
 * @since 2020年7月30日
 */
public abstract class AbstractJobInteceptorChainAdapter extends JobInterceptorChain {
	
	private TaskInterceptorRegister register;

	@PostConstruct
	public void init() {
		register = new TaskInterceptorRegister();
		regInterceptor(register);
		setInterceptors(register.getInterceptorsByOrder());
	}
	
	protected TaskInterceptorRegister getRegister() {
		return register;
	}

	public abstract void regInterceptor(TaskInterceptorRegister register);
	

	public static class TaskInterceptorRegister{
		
		private List<RegInterceptor> regInterceptors;
		
		public TaskInterceptorRegister() {
			super();
			regInterceptors = new ArrayList<>();
		}

		/**
		 * <p>Title: regInterceptor</p>
		 * <p>Description: 注册一个拦截器</p>   
		 * @param interceptor
		 * @param order 优先级
		 */
		public void regInterceptor(JobInterceptor interceptor, int order) {
			regInterceptors.add(new RegInterceptor(order, interceptor));
		}
		protected List<JobInterceptor> getInterceptorsByOrder() {
			return regInterceptors.stream().sorted((t1,t2)->{
				return t1.getOrder()-t2.getOrder();
			}).map(RegInterceptor::getInterceptor).collect(Collectors.toList());
		}
	}
	
	protected static class RegInterceptor{
		private int order;
		private JobInterceptor interceptor;
		public RegInterceptor(int order, JobInterceptor interceptor) {
			super();
			this.order = order;
			this.interceptor = interceptor;
		}
		public int getOrder() {
			return order;
		}
		public void setOrder(int order) {
			this.order = order;
		}
		public JobInterceptor getInterceptor() {
			return interceptor;
		}
		public void setInterceptor(JobInterceptor interceptor) {
			this.interceptor = interceptor;
		}
	}
}
