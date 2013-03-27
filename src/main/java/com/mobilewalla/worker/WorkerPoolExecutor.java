package com.mobilewalla.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mobilewalla.processor.RankStats;

public class WorkerPoolExecutor {
	
	private static final Logger log = LoggerFactory
			.getLogger(WorkerPoolExecutor.class);
	private static ExecutorService executorService = Executors.newFixedThreadPool(30);
	private static ApplicationContext context;

	public static void main(String args[]){
		System.out.println("Start");
		context = new ClassPathXmlApplicationContext(new String [] 
					{ 	"/spring/cassandra.xml",
					  	"/spring/jdbc.xml",
						"/spring/process.xml"});  
		
	    RankStats rankStats = (RankStats) context.getBean("rankStats");
	    String key =null;
	    while (!StringUtils.equals(rankStats.getlastKey(key), key)){
	    	log.debug("processing key {}", key);
	    	executorService.submit(new AppsWorker(key, rankStats));
	    	key = rankStats.getlastKey(key);
	    	log.debug("last key {}", key);
	    	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    executorService.shutdown();
		System.out.println("end");
	
	}
}
