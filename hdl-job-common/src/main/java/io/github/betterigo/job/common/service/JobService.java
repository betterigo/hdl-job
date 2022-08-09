package io.github.betterigo.job.common.service;

import io.github.betterigo.job.common.pojo.JobEntity;

import java.util.List;

/**
 * 任务接口
 * @author hdl
 * @since 2022/8/9 16:30
 */
public interface JobService {

    /**
     * 根据分组名称获取任务列表
     * @param groupName 组名称
     * @return List
     */
    List<JobEntity> listJobsByGroup(String groupName);
}
