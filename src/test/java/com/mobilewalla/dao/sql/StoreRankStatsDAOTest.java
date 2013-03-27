package com.mobilewalla.dao.sql;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mobilewalla.domain.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/jdbc.xml" })
public class StoreRankStatsDAOTest {

	@Autowired
	private StoreRankStatsSqlDAO storeRankStatsDAO;

	@Test
	public void testInsert() {
		assertEquals(1,
				storeRankStatsDAO.insert(1L, "SG", 1L, 1L, new Date(),  "TG"));
		assertEquals(1, storeRankStatsDAO.deleteByAppId(1L));

	}
	
	@Test
	public void testQueryWithNullFeedType() {
		Date date = new Date();
		assertEquals(1,
				storeRankStatsDAO.insert(1L, "SG", 1L, 1L, new Date(),  null));
		Application app = storeRankStatsDAO.getRecord(1L, "SG", 1L, 1L, date,  null);
		assertEquals(Long.valueOf(1L), app.getApplicationId());
		assertEquals(1, storeRankStatsDAO.deleteByAppId(1L));

	}

	
	@Test
	public void testQueryWithFeedType() {
		Date date = new Date();
		assertEquals(1,
				storeRankStatsDAO.insert(1L, "SG", 1L, 1L, new Date(),  "TG"));
		Application app = storeRankStatsDAO.getRecord(1L, "SG", 1L, 1L, date,  "TG");
		assertEquals(Long.valueOf(1L), app.getApplicationId());
		storeRankStatsDAO.deleteByAppId(1L);

	}

}
