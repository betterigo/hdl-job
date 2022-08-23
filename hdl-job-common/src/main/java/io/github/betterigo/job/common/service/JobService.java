package io.github.betterigo.job.common.service;

import io.github.betterigo.job.common.pojo.JobCEntity;
import io.github.betterigo.job.common.pojo.JobEntity;
import org.quartz.Job;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * 任务接口,现在由hdl-job-admin模块实现，该接口提供的任务才做可以在不同的实现组件中统一调用
 * @author hdl
 * @since 2022/8/9 16:30
 */
public interface JobService {

    /**
     * 注册一个任务
     * @param jobCEntity 参数实体
     * @return String
     */
    String registerJob(JobCEntity jobCEntity, Class<? extends Job> jobClass) throws SchedulerException;

    /**
     * 取消一个任务
     * @param jobName 任务名称
     * @param groupName 分组名称
     * @return Boolean
     */
    boolean unRegister(String jobName, String groupName) throws SchedulerException;

    /**
     * 暂停一个任务
     * @param jobName 任务名称
     * @param groupName 分组名称
     * @throws SchedulerException
     */
    void pause(String jobName, String groupName) throws SchedulerException;

    /**
     * 恢复一个暂停的任务
     * @param jobName 任务名称
     * @param groupName 分组名称
     * @throws SchedulerException
     */
    void resume(String jobName, String groupName) throws SchedulerException;

    /**
     * 根据分组获取任务列表
     * @param groupName 分组名称
     * @return List
     */
    List<JobCEntity> getJobs(String groupName);
}
