package io.github.betterigo.job.admin.service.impl;

import io.github.betterigo.job.admin.entity.TaskLog;
import io.github.betterigo.job.admin.mapper.TaskLogMapper;
import io.github.betterigo.job.admin.service.ITaskLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2022-08-08
 */
@Service
public class TaskLogServiceImpl extends ServiceImpl<TaskLogMapper, TaskLog> implements ITaskLogService {

}
