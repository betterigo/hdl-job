/*
 * Copyright (c) 2020. All Rights Reserved.
 * ProjectName:  intelctrl
 * ClassName: PersistTask
 * Author: hdl
 * Date: 2020/10/27 下午3:19
 */

package io.github.betterigo.job.client.common.core.persistant;

import io.github.betterigo.job.client.common.core.annotation.JobRequireBean;
import io.github.betterigo.job.client.common.utils.SpringBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author hdl
 * @description 计划任务构建类，所有的计划任务都从继承{@link PersistJob}这里开始
 * @since 2020/10/27
 */
public abstract class PersistJob {
	
	private static final Logger logger = LoggerFactory.getLogger(PersistJob.class);
	
	private JobInfo jobInfo;
	/**
	 * 计划任务的执行方法
	 */
    public abstract void exec(Map<String, String> metaData);
    
    /**
     * 计划任务的基本信息
     * @return
     */
    protected JobInfo taskInfo() {
    	if(this.jobInfo == null) {
    		this.jobInfo = new JobInfo();
    		configTaskInfo(jobInfo);
    		//处理TaskRequireBean
    		Field[] fields = this.getClass().getDeclaredFields();
    		for(Field f : fields) {
    			if(f.isAnnotationPresent(JobRequireBean.class)) {
    				//加载bean
					JobRequireBean taskRequireBean= f.getAnnotation(JobRequireBean.class);
    				String requestBeanName = taskRequireBean.value();
    				f.setAccessible(true);
    				try {
    					//设置bean
    					if(StringUtils.isEmpty(requestBeanName)) {    						
    						f.set(this, SpringBeanUtil.getBean(f.getType()));
    					}else {
    						f.set(this, SpringBeanUtil.getBean(requestBeanName));
    					}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						logger.error("",e);
					}
    				this.jobInfo.addMetaData(f.getType().getName() + JobInfo.BEAN_NAME_META_SUFFIX, requestBeanName);
    			}
    		}
    	}
    	return this.jobInfo;
    }
    
    /**
     * 配置计划任务的基本信息
     * @param jobInfo
     */
    abstract protected void configTaskInfo(JobInfo jobInfo);
}
