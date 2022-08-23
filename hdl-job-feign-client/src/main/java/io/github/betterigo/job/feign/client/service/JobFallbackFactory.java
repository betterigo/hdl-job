package io.github.betterigo.job.feign.client.service;

import io.github.betterigo.job.client.common.bean.JobBean;
import io.github.betterigo.job.client.common.bean.JobCEntity;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hdl
 * @since 2022/8/11 16:25
 */
@Component
public class JobFallbackFactory implements FallbackFactory<FeignJobService> {
    @Override
    public FeignJobService create(Throwable cause) {
        return new FeignJobService() {
            /**
             * <p>Title: register</p>
             * <p>Description: 注册一个任务</p>
             *
             * @param cron
             * @param taskName
             * @param serviceName
             * @return
             */
            @Override
            public String register(String cron, String taskName, String serviceName) {
                return null;
            }

            @Override
            public String register(JobCEntity jobCEntity) {
                return null;
            }

            /**
             * <p>Title: unRegister</p>
             * <p>Description: 移除一个任务</p>
             *
             * @param taskName
             * @param serviceName
             * @return
             */
            @Override
            public boolean unRegister(String taskName, String serviceName) {
                return false;
            }

            /**
             * <p>Title: ListTaskByGroup</p>
             * <p>Description: 获取任务列表</p>
             *
             * @param serviceName
             * @return
             */
            @Override
            public List<JobBean> ListJobByGroup(String serviceName) {
                return null;
            }

            @Override
            public List<JobCEntity> getJobEntityList(String serviceName) {
                return null;
            }

            /**
             * <p>Title: pauseTask</p>
             * <p>Description: 暂停任务</p>
             *
             * @param taskName
             * @param serviceName
             * @return
             */
            @Override
            public boolean pauseJob(String taskName, String serviceName) {
                return false;
            }

            /**
             * <p>Title: resumeTask</p>
             * <p>Description: 恢复一个任务</p>
             *
             * @param taskName
             * @param serviceName
             * @return
             */
            @Override
            public boolean resumeJob(String taskName, String serviceName) {
                return false;
            }
        };
    }
}
