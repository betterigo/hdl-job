package io.github.betterigo.job.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.betterigo.job.admin.core.TaskStatus;
import io.github.betterigo.job.admin.entity.Task;
import io.github.betterigo.job.admin.service.ITaskService;
import io.github.betterigo.job.common.pojo.JobCEntity;
import io.github.betterigo.job.common.service.JobService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务操作实现类
 * @author hdl
 * @since 2022/8/23 11:08
 */
@Service
public class JobServiceImpl implements JobService {

    private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    @Qualifier("Scheduler")
    private Scheduler scheduler;

    @Autowired
    private ITaskService taskService;

    private static final int TASK_HOLD_TIME_MILLIS = 30 * 1000;
    /**
     * 注册一个任务
     *
     * @param jobCEntity 参数实体
     * @return String
     */
    @Override
    public String registerJob(JobCEntity jobCEntity, Class<? extends Job> jobClass) throws SchedulerException {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("clientName", jobCEntity.getServiceName());
        dataMap.put("cron", jobCEntity.getCron());
        dataMap.putAsString("times", jobCEntity.getTimes());
        dataMap.putAsString("period", jobCEntity.getPeriod());
//		dataMap.put("lb", ribbonLoadBalancerClient);
        dataMap.put("taskClass", jobCEntity.getJobClassName());
        dataMap.put("metaData", JSON.toJSONString(jobCEntity.getMetaData()));
        String jobId = jobCEntity.getJobName();
        dataMap.put("taskName", jobId);
        JobDetail jobDetail = JobBuilder.newJob(jobClass).usingJobData(dataMap).withIdentity(jobId, jobCEntity.getServiceName()).build();
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withDescription(jobCEntity.getServiceName() + " trigger")
                .withIdentity(jobId, jobCEntity.getServiceName());
        if(!StringUtils.isEmpty(jobCEntity.getCron())) {
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(jobCEntity.getCron()));
        }else {
            if(jobCEntity.getTimes()>0) {
                triggerBuilder.withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(jobCEntity.getTimes(), jobCEntity.getPeriod()));
            }else {
                triggerBuilder.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(jobCEntity.getPeriod()));
            }
        }
        synchronized (this) {//可能有线程安全问题
            boolean exist = scheduler.checkExists(jobDetail.getKey());
            if(!exist) {
                scheduler.scheduleJob(jobDetail, triggerBuilder.build());
                log.info("添加定时任务 job:{},trigger-cron:{}",jobId, jobCEntity.getCron());
            }
        }
        return jobId;
    }

    /**
     * 取消一个任务
     *
     * @param jobName  任务名称
     * @param groupName 分组名称
     * @return Boolean
     */
    @Override
    public boolean unRegister(String jobName, String groupName) throws SchedulerException {
        return scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
    }

    /**
     * 暂停一个任务
     *
     * @param jobName   任务名称
     * @param groupName 分组名称
     * @throws SchedulerException
     */
    @Override
    public void pause(String jobName, String groupName) throws SchedulerException {
        scheduler.pauseJob(new JobKey(jobName, groupName));
    }

    /**
     * 恢复一个暂停的任务
     *
     * @param jobName   任务名称
     * @param groupName 分组名称
     * @throws SchedulerException
     */
    @Override
    public void resume(String jobName, String groupName) throws SchedulerException {
        scheduler.resumeJob(new JobKey(jobName, groupName));
    }

    /**
     * 根据分组获取任务列表
     *
     * @param groupName 分组名称
     * @return List
     */
    @Override
    public List<JobCEntity> getJobs(String groupName) {
        QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
        taskQueryWrapper.lambda()
                .eq(!StringUtils.isEmpty(groupName), Task::getGroupName, groupName)
                //2021年10月18日14:39:19添加，对于updateTime和当前时间相差30秒内的任务，应当予以载入，因为可能造成
                //client端无法获取任务的问题
                .ne(Task::getStatus, TaskStatus.STOPPED.getValue())
                .or(wrp -> {
//					wrp.gt(Task::getUpdateTime, new Date(System.currentTimeMillis() - TASK_HOLD_TIME_MILLIS))
//					.or().eq(Task::getUpdateTime, null);
                    wrp.ne(Task::getStatus, TaskStatus.STOPPED.getValue());
                    wrp.and(w->{
                        w.gt(Task::getUpdateTime, new Date(System.currentTimeMillis() - TASK_HOLD_TIME_MILLIS))
                                .or().eq(Task::getUpdateTime, null);
                    });
                });
        List<Task> tasks = taskService.list(taskQueryWrapper);
        List<JobCEntity> result = tasks.stream().map(task -> {
            try {
                JSONObject json = JSONObject.parseObject(new String(task.getMetaData().getBytes(1, (int) task.getMetaData().length()), StandardCharsets.UTF_8));
                JobCEntity jobCEntity = new JobCEntity();
                jobCEntity.setCron(task.getCron());
                Map<String, String> mtd = new HashMap<>();
                for(Map.Entry<String, Object> entry : json.getInnerMap().entrySet()) {
                    mtd.put(entry.getKey(), entry.getValue().toString());
                }
                jobCEntity.setMetaData(mtd);
                jobCEntity.setJobClassName(task.getExecutorClass());
                jobCEntity.setServiceName(task.getGroupName());
                jobCEntity.setJobName(task.getName());
                return jobCEntity;
            }catch (Exception e) {
                log.error("",e);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return result;
    }
}
