package io.github.betterigo.job.feign.config;

import io.github.betterigo.job.common.settings.RemoteJobInfo;
import io.github.betterigo.job.feign.jobs.RemoteFeignJob;

/**
 * @author hdl
 * @since 2022/8/9 11:23
 */
public class RemoteFeignJobInfo implements RemoteJobInfo {
    /**
     * 获取远程任务类的名称
     *
     * @return String
     */
    @Override
    public String remoteJobClassName() {
        return RemoteFeignJob.class.getName();
    }
}
