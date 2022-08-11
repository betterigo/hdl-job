package io.github.betterigo.job.client.common.core.persistant;

import io.github.betterigo.job.client.common.bean.JobEntity;
import io.github.betterigo.job.client.common.core.IJob;
import io.github.betterigo.job.client.common.core.JobRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:加载服务器端的任务列表，用于服务启动时加载计划任务列表或者同步计划任务   
 * @author: haodonglei
 * @date:   2020年11月5日 上午10:17:17     
 * @Copyright: 2020 Inc. All rights reserved.
 */
@Component
public class RemoteJobLoader implements JobLoader {
	
	private static final Logger log = LoggerFactory.getLogger(PersistJobHolder.class);
	
	@Autowired
	private JobRegister taskRegister;
	
	@Override
	public List<IJob> loadTask() {
		List<JobEntity> taskEntities = taskRegister.ListjobEntities();
        if(!CollectionUtils.isEmpty(taskEntities)) {
        	List<IJob> iTasks = taskEntities.stream().map(t->{
        		log.info("获取到远程任务task-name:{}",t.getJobName());
        		//加载任务
        		JobInfo jobInfo = new JobInfo();
        		jobInfo.setCron(t.getCron());
        		jobInfo.setTaskName(t.getJobName());
        		jobInfo.setMetaData(t.getMetaData());
        		try {
        			//构建计划任务
					PersistPlanningJob persistPlanningTask = null;
					try {
						persistPlanningTask = new PersistPlanningJob(jobInfo, t.getJobClassName());
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					return persistPlanningTask;
				} catch (ClassNotFoundException e) {
					log.error("",e);
				}
				return null;
        	}).filter(it->it!=null).collect(Collectors.toList());
        	return iTasks;
        }
		return null;
	}

}
