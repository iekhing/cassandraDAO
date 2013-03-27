package com.mobilewalla.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/cassandra.xml",
									"/spring/jdbc.xml",
									"/spring/process.xml"})
public class RankStatsTest {

	@Autowired
	private RankStats rankStats;
	
	@Test
	public void testProcessAllCrawledData() {
		rankStats.processAllCrawledData("i-324670376-de");
	}
	
	@Test
	public void testRepairData() {
		String [] keys = new String[]{
				"i-521199406-ca"
		};
		for (String key:keys){
			rankStats.processAllCrawledData(key);
		}
	
	}

}
