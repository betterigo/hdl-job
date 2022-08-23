package io.github.betterigo.job.feign.controller;

import io.github.betterigo.job.client.common.bean.JobCEntity;
import io.github.betterigo.job.common.service.JobService;
import io.github.betterigo.job.feign.jobs.RemoteFeignJob;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feign")
public class RegisterEndController {

	private static final Logger log = LoggerFactory.getLogger(RegisterEndController.class);

	@Autowired
	private JobService jobService;


	/**
	 * <p>
	 * Title: register
	 * </p>
	 * <p>
	 * Description: 注册远程任务
	 * 现在的实现方案是，先注册到scheduler里面，然后在listener中的监听中持久化到数据库中。
	 * 这种实现方案存在一个问题需要解决，那就是如果数据库中已经有了该任务应该怎么办的问题
	 * </p>
	 *
	 * @throws SchedulerException
	 */
	@PostMapping("/job/reg-ps")
	public String register(@RequestBody JobCEntity jobCEntity) throws SchedulerException {
		return jobService.registerJob(jobCEntity,RemoteFeignJob.class);
	}

	/**
	 * 取消一个任务
	 * @param taskName 任务名称
	 * @param serviceName 组名称
	 * @return
	 */
	@GetMapping("/job/unreg")
	public boolean unRegister(@RequestParam String taskName,@RequestParam String serviceName) {
		try {
			return jobService.unRegister(taskName,serviceName);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * <p>Title: pauseTask</p>
	 * <p>Description: 暂停任务</p>   
	 * @param taskName 任务名称
	 * @param serviceName 分组名称
	 * @return
	 * @throws SchedulerException 
	 */
	@GetMapping("/job/pause")
	public boolean pauseTask(@RequestParam String taskName,@RequestParam String serviceName) throws SchedulerException {
		jobService.pause(taskName,serviceName);
		return true;
	}

	/**
	 * 恢复一个暂停状态的任务
	 * @param taskName 任务名称
	 * @param serviceName 分组名称
	 * @return
	 * @throws SchedulerException
	 */
	@GetMapping("/job/resume")
	public boolean resumeTask(@RequestParam String taskName,@RequestParam String serviceName) throws SchedulerException {
		jobService.resume(taskName,serviceName);
		return true;
	}

	/**
	 * 获取任务列表
	 * @param serviceName 服务名称（分组名称）
	 * @return
	 */
	@GetMapping("/job/entity")
	public List<JobCEntity> getTaskEntityList(@RequestParam String serviceName){
		return jobService.getJobs(serviceName);
	}
}
