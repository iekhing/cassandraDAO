package com.mobilewalla.dao.cassandra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.mobilewalla.dao.cassandra.MWRankListStatDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/cassandra.xml" })
public class MWRankListStatDAOTest {

	@Autowired
	private MWRankListStatDAO mWRankListStatDAO;

	@Test
	public void getAllTest() {
		Map<String, Map<Long, String>> data = mWRankListStatDAO.getAll();
		assertNotNull(data);
		Assert.notEmpty(data);
	}

	@Test
	public void getNext100Test() {
		Map<String, Map<Long, String>> data = mWRankListStatDAO.getAll();
		Object[] keys = data.keySet().toArray();
		// System.out.println( " "+ keys[1].getClass());
		String lastKey = (String) keys[data.keySet().size() - 1];
		data = mWRankListStatDAO.getAll(lastKey);
		assertNotNull(data);
		Assert.notEmpty(data);

	}

	@Test
	public void getAllColumnByKeyTest() {
		String key = "i-cn-p-6008-free-rank";
		Map<String, Map<Long, String>> data = mWRankListStatDAO.getAllColumnByKey(key);
		assertNotNull(data);
		Assert.notEmpty(data);
		assertEquals(1, data.size());
		assertNotNull(data.get("i-cn-p-6008-free-rank"));
		Map<Long, String> columns = data.get("i-cn-p-6008-free-rank");
		assertEquals(401, columns.size());
	}

}
