package io.github.betterigo.job.feign.client.endpoint;

import io.github.betterigo.job.feign.client.core.RemoteTaskAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: RemoteExecutorEndpoint</p>  
 * <p>Description: 该类将自动注册一个端点用来执行远程任务</p>
 * @author haodonglei
 * @since 2020年7月30日
 */
@RestController
public class RemoteExecutorEndpoint {

	private static final String BASE_URL="/hdl/job";

	@Autowired
	private RemoteTaskAgent taskAgent;
	
	@GetMapping(BASE_URL + "/remote-exec")
	public void execTask(@RequestParam String name) {
		taskAgent.execTask(name);
	}
}
