package io.github.betterigo.job.feign.client.service;

import io.github.betterigo.job.client.common.bean.JobBean;
import io.github.betterigo.job.client.common.bean.JobCEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author hdl
 * @since 2022/8/11 16:23
 */
@FeignClient(name = "hdl-job-admin",fallbackFactory = JobFallbackFactory.class)
public interface FeignJobService {


    /**
     * <p>Title: register</p>
     * <p>Description: 注册一个任务</p>
     * @param cron
     * @param taskName
     * @param serviceName
     * @return
     */
    @GetMapping("/feign/job/reg")
    String register(@RequestParam("cron") String cron,@RequestParam("taskName") String taskName,@RequestParam("serviceName") String serviceName);

    @PostMapping("/feign/job/reg-ps")
    String register(@RequestBody JobCEntity jobCEntity);
    /**
     * <p>Title: unRegister</p>
     * <p>Description: 移除一个任务</p>
     * @param taskName
     * @param serviceName
     * @return
     */
    @GetMapping("/feign/job/unreg")
    boolean unRegister(@RequestParam("taskName") String taskName, @RequestParam("serviceName") String serviceName);

    /**
     * <p>Title: ListTaskByGroup</p>
     * <p>Description: 获取任务列表</p>
     * @param serviceName
     * @return
     */
    @GetMapping("/feign/job/list")
    List<JobBean> ListJobByGroup(@RequestParam("serviceName") String serviceName);

    @GetMapping("/feign/job/entity")
    List<JobCEntity> getJobEntityList(@RequestParam("serviceName") String serviceName);
    /**
     * <p>Title: pauseTask</p>
     * <p>Description: 暂停任务</p>
     * @param taskName
     * @param serviceName
     * @return
     */
    @GetMapping("/feign/job/pause")
    boolean pauseJob(@RequestParam("taskName") String taskName,@RequestParam("serviceName") String serviceName);
    /**
     * <p>Title: resumeTask</p>
     * <p>Description: 恢复一个任务</p>
     * @param taskName
     * @param serviceName
     * @return
     */
    @GetMapping("/feign/job/resume")
    boolean resumeJob(@RequestParam("taskName") String taskName,@RequestParam("serviceName") String serviceName);
}
