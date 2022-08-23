package io.github.betterigo.job.client.demo.jobs;

import io.github.betterigo.job.client.common.core.persistant.JobInfo;
import io.github.betterigo.job.client.common.core.persistant.PersistJob;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hdl
 * @since 2022/8/23 15:25
 */
@Component
public class MyJob extends PersistJob {
    /**
     * 计划任务的执行方法
     *
     * @param metaData
     */
    @Override
    public void exec(Map<String, String> metaData) {
        System.out.println("myJob执行了.name="+metaData.get("name"));
    }

    /**
     * 配置计划任务的基本信息
     *
     * @param jobInfo
     */
    @Override
    protected void configTaskInfo(JobInfo jobInfo) {
        jobInfo.setCron("0/10 * * * * ?");
        jobInfo.setTaskName("my-job");
        jobInfo.addMetaData("name","张三");
    }
}
