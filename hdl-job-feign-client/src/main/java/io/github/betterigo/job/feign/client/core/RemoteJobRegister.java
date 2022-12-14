package io.github.betterigo.job.feign.client.core;

import io.github.betterigo.job.client.common.bean.JobBean;
import io.github.betterigo.job.client.common.core.IJob;
import io.github.betterigo.job.client.common.core.JobRegister;
import io.github.betterigo.job.client.common.core.PlanningJob;
import io.github.betterigo.job.client.common.core.persistant.PersistJob;
import io.github.betterigo.job.client.common.core.persistant.PersistJobHolder;
import io.github.betterigo.job.client.common.core.persistant.PersistPlanningJob;
import io.github.betterigo.job.feign.client.service.FeignJobService;
import io.github.betterigo.job.lib.bean.JobCEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class RemoteJobRegister implements JobRegister {
	
	@Autowired
	private FeignJobService jobService;
	
	@Autowired
	private PersistJobHolder persistJobHolder;
	
	@Value("${spring.application.name}")
	private String serviceName;
	
	private Map<String, IJob> jobMap = new ConcurrentHashMap<>();
	

	@Override
	public void regjob(PlanningJob job) {
		String jobId = jobService.register(job.getCron(), job.getJobName(), serviceName);
		jobMap.put(jobId,job);
	}
	
	@Override
	public void regjob(String cron,String jobName,Runnable job) {
		String jobId = jobService.register(cron, jobName, serviceName);
		jobMap.put(jobId, new PlanningJob(cron, job, jobName));
	}

	@Override
	public void regjob(String cron, String jobName, Runnable job, Map<String, String> metaData) {
		String jobId = jobService.register(cron, jobName, serviceName);
		jobMap.put(jobId, new PlanningJob(cron, job, jobName, metaData));
	}

	@Override
	public IJob getjob(String jobName) {
		return persistJobHolder.getJob(jobName);
	}

	@Override
	public boolean unRegjob(String jobName) {
		boolean result = jobService.unRegister(jobName, serviceName);
		if(result) {			
			jobMap.remove(jobName);
			persistJobHolder.removeJob(jobName);
		}
		return false;
	}
	
	/**
	 * <p>Title: Listjob</p>
	 * <p>Description: ????????????</p>   
	 * @return
	 */
	public List<JobBean> Listjob() {
		return jobService.ListJobByGroup(serviceName);
	}
	
	/**
	 * <p>Title: pausejob</p>
	 * <p>Description:??????????????????</p>   
	 * @param jobName
	 * @return
	 */
	public boolean pausejob(String jobName) {
		return jobService.pauseJob(jobName, serviceName);
	}
	
	/**
	 * <p>Title: resumejob</p>
	 * <p>Description: ???????????????????????????</p>   
	 * @param jobName
	 * @return
	 */
	public boolean resumejob(String jobName) {
		return jobService.resumeJob(jobName, serviceName);
	}

	@Override
	public void regjob(String cron, String jobName, Supplier<?> supplier) {
		String jobId = jobService.register(cron, jobName, serviceName);
		jobMap.put(jobId, new PlanningJob(cron, supplier, jobName));
	}

	@Override
	public void regjob(String cron, String jobName, Supplier<?> supplier, Map<String, String> metaData) {
		String jobId = jobService.register(cron, jobName, serviceName);
		jobMap.put(jobId, new PlanningJob(cron, supplier, jobName, metaData));
	}

	/**
	 * ?????????????????????????????????
	 */
	@Override
	public void regPersistJob(PersistJob persistjob) {
		PersistPlanningJob persistPlanningjob = new PersistPlanningJob(persistjob);
		if(persistJobHolder.getJob(persistPlanningjob.getJobName())!=null) {
//			return;
		}
		JobCEntity jobCEntity = new JobCEntity();
		jobCEntity.setCron(persistPlanningjob.getCron());
		jobCEntity.setMetaData(persistPlanningjob.getMetaData());
		jobCEntity.setServiceName(serviceName);
		jobCEntity.setJobClassName(persistPlanningjob.getJobClass().getName());
		jobCEntity.setJobName(persistPlanningjob.getJobName());
		jobCEntity.setPeriod(persistPlanningjob.getPeriod());
		jobCEntity.setTimes(persistPlanningjob.getTimes());
		persistJobHolder.addJob(jobCEntity.getJobName(), persistPlanningjob);
		jobService.register(jobCEntity);
	}

	@Override
	public List<JobCEntity> ListjobEntities() {
		return jobService.getJobEntityList(serviceName);
	}

	@Override
	public void reloadRemotejob() {
		persistJobHolder.reloadRemoteJob();
	}
}
