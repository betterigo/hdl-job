package io.github.betterigo.job.client.common.core.persistant;

import io.github.betterigo.job.client.common.core.IJob;

import java.util.List;

/**
 * @Description 任务加载接口
 * @author haodonglei
 * @date   2020年11月5日 上午9:59:50     
 * @Copyright 2020 Inc. All rights reserved.
 */
public interface JobLoader {
	/**
	 * @Description 加载计划任务 
	 * @return List<ITask>
	 */
	List<IJob> loadTask();
}
