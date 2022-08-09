package io.github.betterigo.job.common.pojo;


import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2022-08-08
 */
public class JobEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Integer id;

    /**
     * cron字符串
     */
    private String cron;

    /**
     * 周期（秒）
     */
    private Integer period;

    /**
     * 执行的总次数
     */
    private Integer totalTimes;

    /**
     * 已经执行的次数
     */
    private Integer execTimes;

    /**
     * 计划任务名称
     */
    private String name;

    /**
     * 分组（服务名称）
     */
    private String groupName;

    /**
     * 计划任务的状态not_scheduled-新建，scheduling-运行，pause-暂停，stopped-停止
     */
    private String status;

    /**
     * 元数据
     */
    private Blob metaData;

    /**
     * 执行器class
     */
    private String executorClass;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 停止时间
     */
    private LocalDateTime stopTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }
    public Integer getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(Integer totalTimes) {
        this.totalTimes = totalTimes;
    }
    public Integer getExecTimes() {
        return execTimes;
    }

    public void setExecTimes(Integer execTimes) {
        this.execTimes = execTimes;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Blob getMetaData() {
        return metaData;
    }

    public void setMetaData(Blob metaData) {
        this.metaData = metaData;
    }
    public String getExecutorClass() {
        return executorClass;
    }

    public void setExecutorClass(String executorClass) {
        this.executorClass = executorClass;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
        this.stopTime = stopTime;
    }

    @Override
    public String toString() {
        return "Task{" +
            "id=" + id +
            ", cron=" + cron +
            ", period=" + period +
            ", totalTimes=" + totalTimes +
            ", execTimes=" + execTimes +
            ", name=" + name +
            ", groupName=" + groupName +
            ", status=" + status +
            ", metaData=" + metaData +
            ", executorClass=" + executorClass +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", stopTime=" + stopTime +
        "}";
    }
}
