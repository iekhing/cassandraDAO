package com.mobilewalla.worker;

import java.util.concurrent.Callable;

import com.mobilewalla.processor.RankStats;

public class AppsWorker implements Callable<Integer> {
	private RankStats rankStats;
	private String key;
	
	public AppsWorker (String key, RankStats rankStats){
		this.rankStats = rankStats;
		this.key = key;
	}

	public Integer call() throws Exception {
		return rankStats.processAllCrawledData(key);
	}

}
