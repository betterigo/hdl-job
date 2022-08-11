package io.github.betterigo.job.feign.client.core;

/**
 * <p>Title: TaskAgent</p>  
 * <p>Description: 任务远程执行代理</p>
 * @author haodonglei
 * @since 2020年7月24日
 */
public interface RemoteTaskAgent {
	
	public void execTask(String taskName);
}
