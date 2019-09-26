package com.quartz.demo.job;

import org.quartz.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class TestJob2 implements Job {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("开始执行========TestJob2==========" + sdf.format(new Date()));
		try {
			Thread.sleep(70000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("TestJob2这个执行了么？？"+this.getClass().getName() + sdf.format(new Date()));
	}

}
