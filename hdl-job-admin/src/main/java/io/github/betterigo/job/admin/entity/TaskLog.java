package io.github.betterigo.job.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2022-08-08
 */
@TableName("t_task_log")
public class TaskLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 组名（服务名称）
     */
    private String groupName;

    /**
     * 定时任务名称
     */
    private String taskName;

    /**
     * 操作 create，schedule，pause，resume，stop
     */
    private String operation;

    /**
     * 是否成功执行
     */
    private String execResult;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 执行时间
     */
    private LocalDateTime executeTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getExecResult() {
        return execResult;
    }

    public void setExecResult(String execResult) {
        this.execResult = execResult;
    }
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    public LocalDateTime getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(LocalDateTime executeTime) {
        this.executeTime = executeTime;
    }

    @Override
    public String toString() {
        return "TaskLog{" +
            "id=" + id +
            ", groupName=" + groupName +
            ", taskName=" + taskName +
            ", operation=" + operation +
            ", execResult=" + execResult +
            ", errorMsg=" + errorMsg +
            ", executeTime=" + executeTime +
        "}";
    }
}
