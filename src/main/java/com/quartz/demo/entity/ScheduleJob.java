package com.quartz.demo.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.quartz.demo.common.persist.Page;

import java.io.Serializable;

public class ScheduleJob implements Serializable {
	

	private String job_group; // 任务组
	private String job_name; // 任务名
	private String class_name; // 执行任务的类
	private String method_name; // 执行任务的方法名
	private String cron_expression; // cron表达式
	private String description; // 描述
	private String status; // 状态
	/**
	 * 当前实体分页对象
	 */
	@JSONField(serialize = false)
	protected Page<ScheduleJob> page;

	public Page<ScheduleJob> getPage() {
		return page;
	}

	public void setPage(Page<ScheduleJob> page) {
		this.page = page;
	}

	public ScheduleJob() {
	}



	public ScheduleJob(String job_group, String job_name, String class_name, String method_name, String cron_expression,
                       String description, String status) {
		super();
		this.job_group = job_group;
		this.job_name = job_name;
		this.class_name = class_name;
		this.method_name = method_name;
		this.cron_expression = cron_expression;
		this.description = description;
		this.status = status;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getJob_group() {
		return job_group;
	}

	public void setJob_group(String job_group) {
		this.job_group = job_group;
	}

	public String getJob_name() {
		return job_name;
	}

	public void setJob_name(String job_name) {
		this.job_name = job_name;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getMethod_name() {
		return method_name;
	}

	public void setMethod_name(String method_name) {
		this.method_name = method_name;
	}

	public String getCron_expression() {
		return cron_expression;
	}

	public void setCron_expression(String cron_expression) {
		this.cron_expression = cron_expression;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    @Override
    public String toString() {
        return "ScheduleEntity{" +
                "jobName='" + job_name + '\'' +
                ", jobGroup='" + job_group + '\'' +
                ", cronExpression='" + cron_expression + '\'' +
                ", description='" + description + '\'' +
                ", className='" + class_name + '\'' +
                ", methodName='" + method_name + '\'' +
                '}';
    }

}
