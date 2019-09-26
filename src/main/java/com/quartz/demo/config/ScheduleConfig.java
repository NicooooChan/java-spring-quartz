package com.quartz.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.quartz.demo.util.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
* @desctiption
* @author 陈急舟
* @date 2019/9/25 10:20
*/

@Configuration
@DependsOn("springContextHolder")
@Slf4j
public class ScheduleConfig {

    @Value("${spring.quartz.dataSource.myDS.quartzInstanceName}")
    private String quartzInstanceName="springboot-quartz";

    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.quartz.dataSource.myDS.maxConnections}")
    private String maxConnection;
	 /**
     * 设置属性
     * @return
     * @throws IOException
     */
    private Properties quartzProperties() throws IOException {
        Properties prop = new Properties();
        prop.put("quartz.scheduler.instanceName", quartzInstanceName);
        prop.put("org.quartz.scheduler.instanceId", "AUTO");
        prop.put("org.quartz.scheduler.skipUpdateCheck", "true");
        prop.put("org.quartz.scheduler.jmx.export", "true");

        prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        prop.put("org.quartz.jobStore.dataSource", "quartzDataSource");
        prop.put("org.quartz.jobStore.tablePrefix", "soa_qrtz_");
        prop.put("org.quartz.jobStore.isClustered", "true");

        prop.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
        prop.put("org.quartz.jobStore.dataSource", "myDS");
        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");
        prop.put("org.quartz.jobStore.misfireThreshold", "120000");
        prop.put("org.quartz.jobStore.txIsolationLevelSerializable", "true");
        prop.put("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS WHERE LOCK_NAME = ? FOR UPDATE");

        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "10");
        prop.put("org.quartz.threadPool.threadPriority", "5");
        prop.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");

        prop.put("org.quartz.dataSource.myDS.driver", driver);
        prop.put("org.quartz.dataSource.myDS.URL", url);
        prop.put("org.quartz.dataSource.myDS.user", user);
        prop.put("org.quartz.dataSource.myDS.password", password);
        prop.put("org.quartz.dataSource.myDS.maxConnections", maxConnection);

        prop.put("org.quartz.plugin.triggHistory.class", "org.quartz.plugins.history.LoggingJobHistoryPlugin");
        prop.put("org.quartz.plugin.shutdownhook.class", "org.quartz.plugins.management.ShutdownHookPlugin");
        prop.put("org.quartz.plugin.shutdownhook.cleanShutdown", "true");
        return prop;
    }

    @Bean(name = "dataSource")
    @Primary
    public DataSource writeDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            log.info("====初始化Druid数据源：writeDataSource====");
            dataSource.setDriverClassName(driver);
            dataSource.setUrl(url);
            dataSource.setUsername(user);
            dataSource.setPassword(password);
            dataSource.setTimeBetweenEvictionRunsMillis(20000);
            dataSource.setTestOnBorrow(false);
            dataSource.setTestOnReturn(false);
            dataSource.setTestWhileIdle(true);
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setValidationQueryTimeout(60000);
            dataSource.setFilters("stat");
            dataSource.setInitialSize(20);
            dataSource.setMinEvictableIdleTimeMillis(60000);
            dataSource.setMinIdle(40);
            dataSource.setMaxActive(80);
            dataSource.setMaxOpenPreparedStatements(80);
            dataSource.setMaxWait(60000);
            dataSource.setPoolPreparedStatements(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    @Bean
    public SchedulerFactoryBean getSchedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        //QuartzScheduler 延时启动，应用启动完10秒后 QuartzScheduler 再启动
        //factoryBean.setStartupDelay(5);
        //用于quartz集群,加载quartz数据源配置
        factoryBean.setQuartzProperties(quartzProperties());
        DataSource datasource = SpringContextHolder.getBean("dataSource");
       factoryBean.setDataSource(datasource);
        return factoryBean;
    }

}