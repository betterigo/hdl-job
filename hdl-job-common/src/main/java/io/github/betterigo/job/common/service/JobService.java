package io.github.betterigo.job.common.service;

import io.github.betterigo.job.client.common.bean.JobEntity;

import java.util.List;

/**
 * 任务接口，所有远程实现类型的组件（e.g hdl-job-feign）需要实现该接口
 * @author hdl
 * @since 2022/8/9 16:30
 */
public interface JobService {

    /**
     * 根据分组名称获取任务列表
     * @param groupName 组名称
     * @return List
     */
    List<io.github.betterigo.job.common.pojo.JobEntity> listJobsByGroup(String groupName);

    /**
     * 注册一个任务
     * @param jobEntity TaskEntity
     * @return JobEntity
     */
    io.github.betterigo.job.common.pojo.JobEntity register(JobEntity jobEntity);

    /**
     * 移除一个任务
     * @param jobName 任务名称
     * @param groupName 分组名称
     * @return boolean
     */
    boolean unRegister(String jobName,String groupName);

    /**
     * 暂停一个任务
     * @param jobName 任务名称
     * @param groupName 分组名称
     * @return boolean
     */
    boolean pauseJob(String jobName,String groupName);

    /**
     * 恢复一个暂停的任务
     * @param jobName 任务名称
     * @param groupName 分组名称
     * @return boolean
     */
    boolean resumeTask(String jobName,String groupName);
}
