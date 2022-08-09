package io.github.betterigo.job.common.settings;

/**
 * 接口：远程任务信息，所有的远程任务实现插件需要实现该接口，并配置一个实现类的bean
 * @author hdl
 * @since 2022/8/9 11:20
 */
public interface RemoteJobInfo {
    /**
     * 获取远程任务类的名称
     * @return String
     */
    String remoteJobClassName();
}
