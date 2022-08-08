package io.github.betterigo.job.service.impl;

import io.github.betterigo.job.entity.Task;
import io.github.betterigo.job.mapper.TaskMapper;
import io.github.betterigo.job.service.ITaskService;
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
