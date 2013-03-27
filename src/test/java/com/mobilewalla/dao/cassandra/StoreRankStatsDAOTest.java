package com.mobilewalla.dao.cassandra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mobilewalla.domain.Application;
import com.mobilewalla.domain.Rank;
import com.mobilewalla.domain.Snapshot;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/cassandra.xml" })
public class StoreRankStatsDAOTest {
	private static final Logger log = LoggerFactory
			.getLogger(StoreRankStatsDAOTest.class);

	@Autowired
	private StoreRankStatsDAO storeRankStatsDAO;

	@Test
	public void testGetAllColumnByKey() {
		log.info("statrt");
		log.info("start get key  {}", "i-585578191-us");
		Map<Date, Map<String, Integer>> data = storeRankStatsDAO
				.getAllColumnByKey("i-585578191-us");
		assertNotNull(data);
		assertNotEquals(0, data.size());
		Date key = (Date) data.keySet().toArray()[1];
		assertNotNull(key);
		assertNotEquals(0, data.get(key).size());
	}

	@Test
	public void testGetAll() {
		SimpleDateFormat dateFormatted = new SimpleDateFormat( "yyyy-MM-dd-HH");
		List<Application> data = storeRankStatsDAO.getAll("i-585578191-us", 100);
		assertNotNull(data);
		Assert.assertTrue(!data.isEmpty());
		Assert.assertEquals(1001, data.size());
		assertEquals( new Long(585578191l), data.get(0).getApplicationId());
		assertEquals("us", data.get(0).getCountry());
		assertEquals("i", data.get(0).getApplicationPlatform());
		
		Snapshot snapshot = data.get(0).getSnapshot().get(0);
		try {
			assertEquals(dateFormatted.parse("2013-03-21-07"), snapshot.getSnapshopTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Rank rank = snapshot.getRanks().get(0);
		assertEquals(new Long(134), rank.getRank());
		assertEquals(new Long(13021), rank.getCategory());
		assertEquals(null, rank.getFeedType());
		assertEquals("t", rank.getMediaType());
		
	}
	
	@Test
	public void getTheLastKeyTest(){
		String lastKey = storeRankStatsDAO.getLastKey("i-585578191-us");
		List<Application> data = storeRankStatsDAO.getAll("i-585578191-us", 100);
		assertEquals(data.get(data.size()-1).getKey(), lastKey);
	}
	
	

}
