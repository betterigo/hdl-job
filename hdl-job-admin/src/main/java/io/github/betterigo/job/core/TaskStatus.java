package io.github.betterigo.job.core;

public enum TaskStatus {
	/**
	 * 没有执行，但已经在任务队列中
	 */
	NOT_SCHEDULED("not_scheduled"),
	/**
	 * 按照计划执行中
	 */
	SCHEDULING("scheduling"),
	/**
	 * 暂停
	 */
	PAUSE("pause"),
	/**
	 * 停止
	 */
	STOPPED("stopped")
	;
	private String value;

	public String getValue() {
		return value;
	}

	private TaskStatus(String value) {
		this.value = value;
	}
}
