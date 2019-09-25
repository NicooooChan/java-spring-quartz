package com.quartz.demo.web;


import com.quartz.demo.entity.ScheduleJob;
import com.quartz.demo.service.ScheduleJobService;
import com.quartz.demo.util.BaseReturn;
import com.quartz.demo.util.BaseReturnCode;
import com.quartz.demo.common.persist.Page;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 定时器Controller
 * 
 * @author 海乐乐
 * @version 创建时间： 2017年5月09日 上午10:42:24
 */
@RestController
@RequestMapping(value = "scheduleJob")
public class ScheduleJobController{

    @Autowired
    private ScheduleJobService service;

	@PostMapping(value = "findPage")
	public String findPage(String pageNo, String pageSize) {
		// 参数校验
		if (!StringUtils.isNumeric(pageNo) || !StringUtils.isNumeric(pageSize)) {
			return new BaseReturn(BaseReturnCode.PARAMS_ERROR, "pageNo、pageSize参数错误且不能为空").toJSONString();
		}
		List<ScheduleJob> list;
		try {
			list = service.getAllScheduleJob();
			Page<ScheduleJob> page = new Page<ScheduleJob>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
			ScheduleJob job = new ScheduleJob();
			job.setPage(page);
			page.setList(list);
			return new BaseReturn("操作成功", page).toJSONString();
		} catch (Exception e) {
			return new BaseReturn(BaseReturnCode.PROCESS_ERROR, "任务列表获取失败!").toJSONString();
		}
	}

	/**
	* @desctiption add添加
	* @author 陈急舟 
	* @date 2019/9/25 14:36
	*/
	@PostMapping(value = "add")
	public String create(ScheduleJob scheduleEntity) {
		if (StringUtils.isBlank(scheduleEntity.getJob_name())) {
			return new BaseReturn(BaseReturnCode.PARAMS_ERROR, "job_name参数不能为空").toJSONString();
		}
		if (StringUtils.isBlank(scheduleEntity.getJob_group())) {
			return new BaseReturn(BaseReturnCode.PARAMS_ERROR, "job_group参数不能为空").toJSONString();
		}
		if (StringUtils.isBlank(scheduleEntity.getCron_expression())) {
			return new BaseReturn(BaseReturnCode.PARAMS_ERROR, "cron_expression参数不能为空").toJSONString();
		}

		// 判断表达式
		boolean f = CronExpression.isValidExpression(scheduleEntity.getCron_expression());
		if (!f) {
			return new BaseReturn(BaseReturnCode.PROCESS_ERROR, "cron表达式有误，不能被解析！").toJSONString();
		}
		try {
			scheduleEntity.setStatus("1");
			service.add(scheduleEntity);
			return new BaseReturn("保存成功").toJSONString();
		} catch (Exception e) {
			return new BaseReturn(BaseReturnCode.PROCESS_ERROR, "系统错误!").toJSONString();
		}
	}

	/**
	* @desctiption 暂停任务
	* @author 陈急舟 
	* @date 2019/9/25 14:37
	*/
	@PostMapping(value = "stopJob")
	public String stop(ScheduleJob scheduleEntity) {
		// 参数校验
		if (StringUtils.isBlank(scheduleEntity.getJob_name()) || StringUtils.isBlank(scheduleEntity.getJob_group())) {
			return new BaseReturn(BaseReturnCode.PARAMS_ERROR, "job_name，job_group 参数错误且不能为空").toJSONString();
		}
		try {
			service.stopJob(scheduleEntity.getJob_name(), scheduleEntity.getJob_group());
			return new BaseReturn("暂停成功!").toJSONString();
		} catch (Exception e) {
			return new BaseReturn(BaseReturnCode.PROCESS_ERROR, "操作失败!").toJSONString();
		}
	}

	/**
	* @desctiption 删除任务
	* @author 陈急舟 
	* @date 2019/9/25 14:37
	*/
	@PostMapping(value = "delete")
	public String delete(ScheduleJob scheduleEntity) {
		if (StringUtils.isBlank(scheduleEntity.getJob_name()) || StringUtils.isBlank(scheduleEntity.getJob_group())) {
			return new BaseReturn(BaseReturnCode.PARAMS_ERROR, "参数job_name和job_group不能为空").toJSONString();
		}
		try {
			service.delJob(scheduleEntity.getJob_name(), scheduleEntity.getJob_group());
			return new BaseReturn("删除成功!").toJSONString();
		} catch (Exception e) {
			return new BaseReturn(BaseReturnCode.PROCESS_ERROR, "操作失败!").toJSONString();
		}
	}

	/**
	* @desctiption 修改表达式
	* @author 陈急舟 
	* @date 2019/9/25 14:37
	*/
	@PostMapping(value = "update")
	public String update(ScheduleJob scheduleEntity) {
		if (StringUtils.isBlank(scheduleEntity.getJob_name())) {
			return new BaseReturn(BaseReturnCode.PARAMS_ERROR, "job_name参数不能为空").toJSONString();
		}
		if (StringUtils.isBlank(scheduleEntity.getJob_group())) {
			return new BaseReturn(BaseReturnCode.PARAMS_ERROR, "job_group参数不能为空").toJSONString();
		}
		if (StringUtils.isBlank(scheduleEntity.getCron_expression())) {
			return new BaseReturn(BaseReturnCode.PARAMS_ERROR, "cron_expression参数不能为空").toJSONString();
		}
		// 验证cron表达式
		boolean f = CronExpression.isValidExpression(scheduleEntity.getCron_expression());
		if (!f) {
			return new BaseReturn(BaseReturnCode.PROCESS_ERROR, "cron表达式有误，不能被解析！").toJSONString();
		}
		try {
			service.modifyTrigger(scheduleEntity.getJob_name(), scheduleEntity.getJob_group(),
					scheduleEntity.getCron_expression());
			return new BaseReturn("操作成功").toJSONString();
		} catch (Exception e) {
			return new BaseReturn(BaseReturnCode.PROCESS_ERROR, "操作失败!").toJSONString();
		}
	}

	/**
	* @desctiption 立即运行一次
	* @author 陈急舟 
	* @date 2019/9/25 14:37
	*/
	@PostMapping(value = "startNow")
	public String stratNow(ScheduleJob scheduleEntity) {
		if (StringUtils.isBlank(scheduleEntity.getJob_name()) || StringUtils.isBlank(scheduleEntity.getJob_group())) {
			return new BaseReturn(BaseReturnCode.PARAMS_ERROR, "参数job_name和job_group不能为空").toJSONString();
		}
		try {
			service.startNowJob(scheduleEntity.getJob_name(), scheduleEntity.getJob_group());
			return new BaseReturn("运行成功").toJSONString();
		} catch (Exception e) {
			return new BaseReturn(BaseReturnCode.PROCESS_ERROR, "操作失败!").toJSONString();
		}
	}

	/**
	* @desctiption 恢复
	* @author 陈急舟 
	* @date 2019/9/25 14:37
	*/
	@PostMapping(value = "resume")
	public String resume(ScheduleJob scheduleEntity) {
		if (StringUtils.isBlank(scheduleEntity.getJob_name()) || StringUtils.isBlank(scheduleEntity.getJob_group())) {
			return new BaseReturn(BaseReturnCode.PARAMS_ERROR, "参数job_name和job_group不能为空").toJSONString();
		}
		try {
			service.restartJob(scheduleEntity.getJob_name(), scheduleEntity.getJob_group());
			return new BaseReturn("恢复成功").toJSONString();
		} catch (Exception e) {
			return new BaseReturn(BaseReturnCode.PROCESS_ERROR, "操作失败!").toJSONString();
		}
	}

	/**
	* @desctiption 获取所有trigger触发器
	* @author 陈急舟 
	* @date 2019/9/25 14:38
	*/
	@PostMapping(value = "getTriggers")
	public String getTriggers(HttpServletRequest request, String pageNo, String pageSize) {
		List<ScheduleJob> list;
		try {
			list = service.getTriggersInfo();
			Page<ScheduleJob> page = new Page<ScheduleJob>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
			ScheduleJob job = new ScheduleJob();
			job.setPage(page);
			page.setList(list);
			return new BaseReturn("操作成功", page).toJSONString();
		} catch (Exception e) {
			return new BaseReturn(BaseReturnCode.PROCESS_ERROR, "操作失败!").toJSONString();
		}

	}
}
