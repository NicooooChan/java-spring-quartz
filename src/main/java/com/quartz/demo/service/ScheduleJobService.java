package com.quartz.demo.service;

import com.quartz.demo.entity.ScheduleJob;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 定时器quartz Service
 *
 * @author 海乐乐
 * @version 2017-05-09
 */
@Service
public class ScheduleJobService{

    @Autowired
    private Scheduler scheduler;

   /**
   * @desctiption 添加定时任务
   * @author 陈急舟 
   * @date 2019/9/25 14:29
   */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void add(ScheduleJob scheduleEntity) throws ClassNotFoundException, SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(scheduleEntity.getClass_name()))
                .withIdentity(scheduleEntity.getJob_name(), scheduleEntity.getJob_group())
                .usingJobData("className", scheduleEntity.getClass_name())
                .usingJobData("methodName", scheduleEntity.getMethod_name()).build();
        jobDetail.getJobDataMap().put("scheduleEntity", scheduleEntity);

        // 表达式调度构建器（可判断创建SimpleScheduleBuilder）

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleEntity.getCron_expression());

        // 按新的cronExpression表达式构建一个新的trigger

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(scheduleEntity.getJob_name(), scheduleEntity.getJob_group()).withSchedule(scheduleBuilder)
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
    * @desctiption 获取所有JobDetail
    * @author 陈急舟 
    * @date 2019/9/25 14:29
    */
    public List<JobDetail> getJobs() {
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            List<JobDetail> jobDetails = new ArrayList<JobDetail>();
            for (JobKey key : jobKeys) {
                jobDetails.add(scheduler.getJobDetail(key));
            }
            return jobDetails;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
    * @desctiption 获取所有计划中的任务
    * @author 陈急舟 
    * @date 2019/9/25 14:29
    */
    public List<ScheduleJob> getAllScheduleJob() {
        List<ScheduleJob> scheduleEntityList = new ArrayList<ScheduleJob>();
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    ScheduleJob scheduleEntity = new ScheduleJob();
                    scheduleEntity.setJob_name(jobKey.getName());
                    scheduleEntity.setJob_group(jobKey.getGroup());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    scheduleEntity.setStatus(triggerState.name());
                    // 获取要执行的定时任务类名

                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    // 获取class及method
                    JobDataMap dataMap = jobDetail.getJobDataMap();
                    scheduleEntity.setClass_name(dataMap.getString("className"));
                    scheduleEntity.setMethod_name(dataMap.getString("methodName"));

                    // scheduleEntity.setClassName(jobDetail.getJobClass().getName());
                    // 判断trigger
                    if (trigger instanceof SimpleTrigger) {
                        SimpleTrigger simple = (SimpleTrigger) trigger;
                        scheduleEntity.setCron_expression(
                                "重复次数:" + (simple.getRepeatCount() == -1 ? "无限" : simple.getRepeatCount()) + ",重复间隔:"
                                        + (simple.getRepeatInterval() / 1000L));
                        scheduleEntity.setDescription(simple.getDescription());
                    }
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cron = (CronTrigger) trigger;
                        scheduleEntity.setCron_expression(cron.getCronExpression());
                        scheduleEntity.setDescription(
                                cron.getDescription() == null ? ("触发器:" + trigger.getKey()) : cron.getDescription());
                    }
                    scheduleEntityList.add(scheduleEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scheduleEntityList;
    }

    /**
    * @desctiption 获取所有运行中的任务
    * @author 陈急舟 
    * @date 2019/9/25 14:29
    */
    public List<ScheduleJob> getAllRuningScheduleJob() {
        List<ScheduleJob> scheduleEntityList = null;
        try {
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            scheduleEntityList = new ArrayList<ScheduleJob>(executingJobs.size());
            for (JobExecutionContext executingJob : executingJobs) {
                ScheduleJob scheduleEntity = new ScheduleJob();
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                scheduleEntity.setJob_name(jobKey.getName());
                scheduleEntity.setJob_group(jobKey.getGroup());
                // scheduleEntity.setDescription("触发器:" + trigger.getKey());

                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                scheduleEntity.setStatus(triggerState.name());
                // 获取要执行的定时任务类名

                scheduleEntity.setClass_name(jobDetail.getJobClass().getName());
                // 判断trigger

                if (trigger instanceof SimpleTrigger) {
                    SimpleTrigger simple = (SimpleTrigger) trigger;
                    scheduleEntity.setCron_expression(
                            "重复次数:" + (simple.getRepeatCount() == -1 ? "无限" : simple.getRepeatCount()) + ",重复间隔:"
                                    + (simple.getRepeatInterval() / 1000L));
                    scheduleEntity.setDescription(simple.getDescription());
                }
                if (trigger instanceof CronTrigger) {
                    CronTrigger cron = (CronTrigger) trigger;
                    scheduleEntity.setCron_expression(cron.getCronExpression());
                    scheduleEntity.setDescription(cron.getDescription());
                }
                scheduleEntityList.add(scheduleEntity);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return scheduleEntityList;
    }

    /**
    * @desctiption 获取所有的触发器
    * @author 陈急舟 
    * @date 2019/9/25 14:29
    */
    public List<ScheduleJob> getTriggersInfo() {
        try {
            GroupMatcher<TriggerKey> matcher = GroupMatcher.anyTriggerGroup();
            Set<TriggerKey> Keys = scheduler.getTriggerKeys(matcher);
            List<ScheduleJob> triggers = new ArrayList<ScheduleJob>();

            for (TriggerKey key : Keys) {
                Trigger trigger = scheduler.getTrigger(key);
                ScheduleJob scheduleEntity = new ScheduleJob();
                scheduleEntity.setJob_name(trigger.getJobKey().getName());
                scheduleEntity.setJob_group(trigger.getJobKey().getGroup());
                scheduleEntity.setStatus(scheduler.getTriggerState(key) + "");
                if (trigger instanceof SimpleTrigger) {
                    SimpleTrigger simple = (SimpleTrigger) trigger;
                    scheduleEntity.setCron_expression(
                            "重复次数:" + (simple.getRepeatCount() == -1 ? "无限" : simple.getRepeatCount()) + ",重复间隔:"
                                    + (simple.getRepeatInterval() / 1000L));
                    scheduleEntity.setDescription(simple.getDescription());
                }
                if (trigger instanceof CronTrigger) {
                    CronTrigger cron = (CronTrigger) trigger;
                    scheduleEntity.setCron_expression(cron.getCronExpression());
                    scheduleEntity.setDescription(cron.getDescription());
                }
                triggers.add(scheduleEntity);
            }
            return triggers;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
    * @desctiption 暂停任务
    * @author 陈急舟 
    * @date 2019/9/25 14:29
    */
    public void stopJob(String name, String group) throws SchedulerException {
        JobKey key = new JobKey(name, group);
        scheduler.pauseJob(key);
    }

    /**
    * @desctiption 恢复任务
    * @author 陈急舟 
    * @date 2019/9/25 14:30
    */
    public void restartJob(String name, String group) throws SchedulerException {
        JobKey key = new JobKey(name, group);
        scheduler.resumeJob(key);
    }

    /**
    * @desctiption 立马执行一次任务
    * @author 陈急舟 
    * @date 2019/9/25 14:30
    */
    public void startNowJob(String name, String group) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(name, group);
        scheduler.triggerJob(jobKey);
    }

    /**
    * @desctiption 删除任务
    * @author 陈急舟 
    * @date 2019/9/25 14:30
    */
    public void delJob(String name, String group) throws SchedulerException {
        JobKey key = new JobKey(name, group);
        scheduler.deleteJob(key);
    }

    /**
    * @desctiption 修改触发器时间
    * @author 陈急舟 
    * @date 2019/9/25 14:30
    */
    public void modifyTrigger(String name, String group, String cron) throws SchedulerException {
        TriggerKey key = TriggerKey.triggerKey(name, group);
        // Trigger trigger = scheduler.getTrigger(key);

        CronTrigger newTrigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity(key)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        scheduler.rescheduleJob(key, newTrigger);
    }

    /**
    * @desctiption 暂停调度器
    * @author 陈急舟 
    * @date 2019/9/25 14:30
    */
    public void stopScheduler() throws SchedulerException {
        if (!scheduler.isInStandbyMode()) {
            scheduler.standby();
        }
    }
}