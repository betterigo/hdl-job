/*
 * Copyright (c) 2020. All Rights Reserved.
 * ProjectName:  intelctrl
 * ClassName: PersistPlanningTask
 * Author: hdl
 * Date: 2020/10/27 下午3:18
 */

package io.github.betterigo.job.client.common.core.persistant;

import io.github.betterigo.job.client.common.core.IJob;
import io.github.betterigo.job.client.common.core.annotation.JobRequireBean;
import io.github.betterigo.job.client.common.utils.SpringBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author hdl
 * @description 可持久化的计划任务，该类的实例由{@link PersistJob}构建，该类提供了可执行的消费对象
 * @since 2020/10/27
 */
public class PersistPlanningJob implements IJob {
	
	private static final Logger logger = LoggerFactory.getLogger(PersistPlanningJob.class);
	
    /**
     * cron
     */
    private String cron;
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 元数据，只能存储String类型的数据，保证可以序列化
     */
    private Map<String, String> metaData;
    /**
     * 执行类
     */
    private Class<?> jobClass;

    private Consumer<Map<String, String>> consumer;

    private PersistJob persistJob;

    private int times;
    
    private int period;
    
    public PersistPlanningJob() {
		super();
	}
    /**
     * 使用persistJob 构造计划任务实例
     * @param persistJob
     */
	public PersistPlanningJob(PersistJob persistJob) {
		super();
		this.persistJob = persistJob;
		JobInfo jobInfo = this.persistJob.taskInfo();
		this.cron = jobInfo.getCron();
		this.metaData = jobInfo.getMetaData();
		this.jobClass = this.persistJob.getClass();
		this.jobName = jobInfo.getTaskName();
		this.period = jobInfo.getPeriod();
		this.times = jobInfo.getTimes();
	}
	
	 public PersistPlanningJob(JobInfo jobInfo, String taskClassName) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		super();
		Class<?> taskClazz = Class.forName(taskClassName);
		Object ins = null;
		try {
			ins = taskClazz.newInstance();
			this.persistJob = (PersistJob) ins;
		} catch (Exception e) {
			logger.error("",e);
		}
		//需要使用taskClass构建执行对象
		this.cron = jobInfo.getCron();
		this.metaData = jobInfo.getMetaData();
		this.jobClass = taskClazz;
		this.jobName = jobInfo.getTaskName();
		this.period = jobInfo.getPeriod();
		this.times = jobInfo.getTimes();
		//处理metaData里面的bean
		if(this.metaData == null) {
			this.metaData = new HashMap<>();
		}
//		List<String> beanNameKeys = this.metaData.keySet().stream().filter(key->key.endsWith(TaskInfo.BEAN_NAME_META_SUFFIX)).collect(Collectors.toList());
//		if(!CollectionUtils.isEmpty(beanNameKeys)) {//如果有bean，需要注入该bean
			Field[] fields = taskClazz.getDeclaredFields();
			for(Field f : fields) {
				if(f.isAnnotationPresent(JobRequireBean.class)) {
					//加载bean
					JobRequireBean taskRequireBean= f.getAnnotation(JobRequireBean.class);
    				String requestBeanName = taskRequireBean.value();
    				f.setAccessible(true);
    				try {
    					//设置bean
    					if(StringUtils.isEmpty(requestBeanName)) {    						
    						f.set(ins, SpringBeanUtil.getBean(f.getType()));
    					}else {
    						f.set(ins, SpringBeanUtil.getBean(requestBeanName));
    					}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						logger.error("",e);
					}
				}
			}
//		}
	}

	public String getCron() {
        return cron;
    }


	@Override
    public Consumer<Map<String, String>> getJob() {
        if(this.consumer == null){
            this.consumer = (metaData)-> {
                persistJob.exec(metaData);
            };
        }
        return this.consumer;
    }

    public String getJobName() {
        return jobName;
    }

    public Map<String, String> getMetaData() {
		return metaData;
	}

	public Class<?> getJobClass() {
        return jobClass;
    }

	public PersistJob getpersistJob() {
		return persistJob;
	}
	public int getTimes() {
		return times;
	}
	public int getPeriod() {
		return period;
	}

}
