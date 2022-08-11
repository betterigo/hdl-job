/*
 * Copyright (c) 2020. All Rights Reserved.
 * ProjectName:  intelctrl
 * ClassName: ITask
 * Author: hdl
 * Date: 2020/10/27 下午3:37
 */

package io.github.betterigo.job.client.common.core;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author hdl
 * @description TODO
 * @since 2020/10/27
 */
public interface IJob {
    String getJobName();
    Map<String, String> getMetaData();
    String getCron();
    Consumer<Map<String, String>> getJob();
}
