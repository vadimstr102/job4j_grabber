package ru.job4j.grabber;

import com.sun.net.httpserver.HttpServer;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private static final Logger LOG = LoggerFactory.getLogger(Grabber.class.getName());
    private final Properties cfg = new Properties();

    public Store store() {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg() throws IOException {
        try (InputStream in = Grabber.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            cfg.load(in);
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        data.put("find", cfg.getProperty("find"));
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            String find = (String) map.get("find");
            List<Post> allPosts = new ArrayList<>();
            List<Post> validPosts = new ArrayList<>();
            Date lastDate = null;
            // Get the latest date from the database if it is not empty
            if (store.getAll().size() != 0) {
                lastDate = store.getLastDate();
            }
            // Number of pages checked
            int numPage = 5;
            try {
                for (int i = 1; i <= numPage; i++) {
                    allPosts.addAll(parse.list("https://www.sql.ru/forum/job-offers/" + i));
                }
                for (Post post : allPosts) {
                    if (post.getName().toLowerCase().matches(".*" + find + "(\\b|\\W|_).*")) {
                        post = parse.detail(post.getLink());
                        if (lastDate != null) {
                            if (post.getCreated().after(lastDate)) {
                                validPosts.add(post);
                            }
                        } else {
                            validPosts.add(post);
                        }
                    }
                }
                if (validPosts.size() != 0) {
                    store.saveAll(validPosts);
                } else {
                    LOG.info("Vacancies not found");
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void web(Store store) throws IOException {
        int port = Integer.parseInt(cfg.getProperty("port"));
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(port), 0);
        server.createContext("/", new GrabberHttpHandler(store));
        server.setExecutor(null);
        server.start();
        LOG.info("Server started on port {}", port);
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(), store, scheduler);
        grab.web(store);
    }
}
