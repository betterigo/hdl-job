package io.github.betterigo.job.client.common.core;

import io.github.betterigo.job.client.common.bean.JobBean;
import io.github.betterigo.job.client.common.core.persistant.PersistJob;
import io.github.betterigo.job.lib.bean.JobCEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>Title: jobRegister</p>  
 * <p>Description: 任务注册器</p>
 * @author haodonglei
 * @since 2020年7月29日
 */
public interface JobRegister {
	@Deprecated
	public void regjob(PlanningJob job);
	/**
	 * <p>Title: regjob</p>
	 * <p>Description:注册一个任务</p>   
	 * @param cron
	 * @param jobName
	 * @param job
	 */
	@Deprecated
	public void regjob(String cron,String jobName,Runnable job);
	
	@Deprecated
	public void regjob(String cron,String jobName,Supplier<?> supplier);
	
	/**
	 * <p>Title: regjob</p>
	 * <p>Description: </p>   
	 * @param cron
	 * @param jobName
	 * @param job
	 * @param metaData 可能用的额外数据
	 */
	@Deprecated
	public void regjob(String cron,String jobName,Runnable job,Map<String, String> metaData);
	
	@Deprecated
	public void regjob(String cron,String jobName,Supplier<?> supplier,Map<String, String> metaData);
	/**
	 * <p>Title: getjob</p>
	 * <p>Description:根据任务名称获取job的Runnable </p>   
	 * @param jobName
	 * @return
	 */
	public IJob getjob(String jobName);
	/**
	 * <p>Title: unRegjob</p>
	 * <p>Description:注销一个任务</p>   
	 * @param jobName
	 * @return
	 */
	@Deprecated
	public boolean unRegjob(String jobName);
	
	/**
	 * <p>Title: Listjob</p>
	 * <p>Description: 任务列表</p>   
	 * @return
	 */
	public List<JobBean> Listjob();
	
	/**
	 * <p>Title: Listjob</p>
	 * <p>Description: 任务列表-持久化</p>   
	 * @return
	 */
	public List<JobCEntity> ListjobEntities();
	
	/**
	 * <p>Title: pausejob</p>
	 * <p>Description:暂停一个任务</p>   
	 * @param jobName
	 * @return
	 */
	public boolean pausejob(String jobName);
	
	/**
	 * <p>Title: resumejob</p>
	 * <p>Description: 恢复一个暂停的任务</p>   
	 * @param jobName
	 * @return
	 */
	public boolean resumejob(String jobName);
	/**
	 * 注册一个可持久化的任务
	 */
	void regPersistJob(PersistJob persistjob);
	/**
	 * 重新加载远程任务
	 */
	void reloadRemotejob();
	
}
