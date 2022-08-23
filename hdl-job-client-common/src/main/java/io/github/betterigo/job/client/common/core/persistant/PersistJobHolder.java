/*
 * Copyright (c) 2020. All Rights Reserved.
 * ProjectName:  intelctrl
 * ClassName: PersistTaskMapper
 * Author: hdl
 * Date: 2020/10/27 下午4:21
 */

package io.github.betterigo.job.client.common.core.persistant;

import io.github.betterigo.job.client.common.core.IJob;
import io.github.betterigo.job.client.common.core.JobRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hdl
 * @description 用于注入PersistTask的bean，用于获取PersistPlanningTask的创建
 * @since 2020/10/27
 */
@Service
public class PersistJobHolder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(PersistJobHolder.class);
    
    @Autowired(required = false)
    private List<PersistJob> tasks;

    @Autowired
    private JobLoader jobLoader;
    @Autowired
    private JobRegister taskRegister;
    
    private Map<String, IJob> taskMap = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unused")
    //TODO，添加单例模式，可以优化执行效率
    //用于存储任务名与对应的任务类名的对应关系。key=任务名，value=任务类名，当使用任务名称查询
    //执行对象的时候，先通过该map查询到对应的任务类名，然后通过类名再去taskMap查询对应的执行类
    //如果不属于单例执行模式的，对应的value值应该为null,则直接去taskmap中使用任务名查询。
    //为防止名称冲突，需要在taskMap的key中添加后缀标识。_TaskName
	private Map<String, String> taskNameMap = new ConcurrentHashMap<>();
    
    public PersistJobHolder() {
		super();
	}
	/**
     * 通过类获取计划任务的执行对象
     * @param clazz
     * @return
     */
    public PersistJob getPersistTaskByClass(Class<?> clazz){
        for(PersistJob task : tasks){
            if(task.getClass().equals(clazz)){
                return task;
            }
        }
        return null;
    }
    public void addJob(String key, IJob task) {
    	taskMap.put(key, task);
    }
    public IJob getJob(String key) {
    	return taskMap.get(key);
    }
    public void removeJob(String key){
        taskMap.remove(key);
    }

    /**
     *
     * 在项目启动后重建计划任务的ITask对象
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
    	log.info("设置时区：Asia/Shanghai");
    	TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        log.info("初始化计划任务的实例对象");
        log.info("加载远程任务...");
        //设置时区为Asia/Shanghai
        try {
            reloadRemoteJob();
        }catch (Exception e){
            log.error("加载任务异常:{}",e.getMessage());
        }
        //TODO 需要判断加载过来的是否包含了tasks里面的任务，如果有的话就不再次注册了
        if(tasks!=null) {
        	log.info("获取{}个任务实例",tasks.size());
        	tasks.stream().forEach(plan->{
        		try {					
        			taskRegister.regPersistJob(plan);
				} catch (Throwable e) {
					log.error("加载任务异常:{}",e.getMessage());
				}
        	});
        }else {
        	log.info("获取0个任务实例");
        }
    }
    /**
     * @Description 重新加载任务
     */
	public void reloadRemoteJob() {
		List<IJob> loadtasks = jobLoader.loadTask();
        //加载远程任务
        if(!CollectionUtils.isEmpty(loadtasks)) {
//            taskMap.clear();
        	loadtasks.stream().forEach(lt->{
        		taskMap.putIfAbsent(lt.getJobName(), lt);
        		log.info("加载远程任务:{}",lt.getJobName());
        	});
        }
	}
}
