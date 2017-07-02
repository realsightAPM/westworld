package com.realsight.westworld.engine.job;

//import com.realsight.westworld.engine.message.JobMessage;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by jiajia on 17/3/28.
 */
public class JobManager {
    private static JobManager jobManager;
    private static SchedulerFactory schedulerFactory;

    private JobManager() {
    }

    public static JobManager getInstance() {
        if (jobManager == null) {
            jobManager = new JobManager();
            schedulerFactory = new StdSchedulerFactory();
            try {
                schedulerFactory.getScheduler().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jobManager;
    }

    public Scheduler get_scheduler() {
        try {
            return schedulerFactory.getScheduler();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String register_job(String jobType) {
        String jobID = UUID.randomUUID().toString();
//        JobMessage jm = new JobMessage(jobID, jobType);

        //new File(String.format("job/%1$s", uniqueID)).mkdirs();
        try {
            JobDetail jobDetail = JobBuilder.newJob(
                    (Class<? extends Job>) Class.forName(JobManager.getInstance().getClass().getPackage().getName() + "." +jobType)
            ).withIdentity(jobID, jobType)
                    .usingJobData("jobType", jobType)
//                    .usingJobData("jobMessage", jm.get_message())
                    .storeDurably()
                    .build();
            JobManager.getInstance().get_scheduler().addJob(jobDetail, true);
            return jobID;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String schedule_job(String jobID, String jobType) {

        Trigger trigger = TriggerBuilder
            .newTrigger()
            .withIdentity(jobID, jobType)
            .startNow()
            .withSchedule(
                    SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(10).repeatForever() // 时间间隔
            ).build();
        try {
            JobManager.getInstance().get_scheduler().scheduleJob(trigger);
        } catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    public String get_job_status() {
        return "";
    }

    public static boolean is_job_running(JobExecutionContext ctx, String jobName, String groupName)
            throws SchedulerException {
        List<JobExecutionContext> currentJobs = ctx.getScheduler().getCurrentlyExecutingJobs();

        for (JobExecutionContext jobCtx : currentJobs) {
            String thisJobName = jobCtx.getJobDetail().getKey().getName();
            String thisGroupName = jobCtx.getJobDetail().getKey().getGroup();
            if (jobName.equalsIgnoreCase(thisJobName) && groupName.equalsIgnoreCase(thisGroupName)
                    && !jobCtx.getFireTime().equals(ctx.getFireTime())) {
                return true;
            }
        }
        return false;
    }

    public void generate_test_data(){
        Trigger trigger1 = TriggerBuilder
                .newTrigger()
                .withIdentity("Metric", "Fake")
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(600).repeatForever() // 时间间隔
                ).build();

        Trigger trigger2 = TriggerBuilder
                .newTrigger()
                .withIdentity("Log", "Fake")
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(600).repeatForever() // 时间间隔
                ).build();

//        JobDetail jobDetail1 = JobBuilder.newJob(MetricFakeJob.class).withIdentity("Metric", "Fake").build();
//        JobDetail jobDetail2 = JobBuilder.newJob(LogFakeJob.class).withIdentity("Log", "Fake").build();
        try {
//            JobManager.getInstance().get_scheduler().scheduleJob(jobDetail1, trigger1);
//            JobManager.getInstance().get_scheduler().scheduleJob(jobDetail2, trigger2);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        JobManager jm = new JobManager();
        //jm.register_job("TestJob");

        String jobType = "TimeSeriesPredictionJob";
        String jobID = jm.register_job(jobType);
        if(jobID == null){
            System.err.println("Register Job Error");
            return;
        }
        jm.schedule_job(jobID, jobType);

        //jm.generate_test_data();

    }
}
