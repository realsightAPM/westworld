package com.realsight.westworld.engine.service;

import com.realsight.westworld.engine.job.JobManager;
import com.realsight.westworld.engine.job.TimeSeriesPredictionJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

@SuppressWarnings("deprecation")
@Service
public class TimeSeriesPredictionService {

    public String get_version() {
        return "1.0.0";
    }

    public boolean start() {
	    try {
            JobDetail jobDetail = JobBuilder.newJob(TimeSeriesPredictionJob.class)
                    .withIdentity("Job_1", "tsp").build();

            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("trigger_1", "tsp")
                    .startNow()
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(60).repeatForever() // 时间间隔
                    ).build();
            JobManager.getInstance().get_scheduler().scheduleJob(jobDetail, trigger);
        } catch (Exception e){
	        e.printStackTrace();
	        return false;
        }
        return true;
    }

	public static void main(String[] args) {

	}
}
