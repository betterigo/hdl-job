package io.github.betterigo.job.admin.service.impl;

import io.github.betterigo.job.admin.entity.Task;
import io.github.betterigo.job.admin.mapper.TaskMapper;
import io.github.betterigo.job.admin.service.ITaskService;
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
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {

}
