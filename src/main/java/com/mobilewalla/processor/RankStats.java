package com.mobilewalla.processor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobilewalla.dao.cassandra.StoreRankStatsDAO;
import com.mobilewalla.dao.sql.StoreRankStatsSqlDAO;
import com.mobilewalla.domain.Application;
import com.mobilewalla.domain.Rank;
import com.mobilewalla.domain.Snapshot;
import com.mobilewalla.worker.WorkerPoolExecutor;

public class RankStats {
	private static final Logger log = LoggerFactory
			.getLogger(RankStats.class);

	private StoreRankStatsSqlDAO sqlstoreRankStatsDAO;
	private StoreRankStatsDAO storeRankStatsRawDao;

	private Integer insertRankStats(List<Application> appsStats) {
		Integer insertCounter = 0;
		for (Application appStat : appsStats) {
			for (Snapshot snapshot : appStat.getSnapshot()) {
				for (Rank rank : snapshot.getRanks()) {
					Application appDb = sqlstoreRankStatsDAO.getRecord(
							appStat.getApplicationId(), appStat.getCountry(),
							rank.getRank(), rank.getCategory(),
							snapshot.getSnapshopTime(), rank.getFeedType());
					if (appDb == null) {
						
						insertCounter++;
						log.info("insert {} counter {}", appStat.getKey(),insertCounter );
						sqlstoreRankStatsDAO.insert(appStat.getApplicationId(),
								appStat.getCountry(), rank.getRank(),
								rank.getCategory(), snapshot.getSnapshopTime(),
								rank.getFeedType());
					}else{
						log.info("skip {} counter {}", appStat.getKey(),insertCounter );
					}
				}

			}
		}
		return insertCounter;
	}

	public Integer processAllCrawledData(String key) {
		List<Application> applications = storeRankStatsRawDao.getAll(key, 500);
		log.info("process {} rows ", applications.size());
		return insertRankStats(applications);
	}

	public String getlastKey(String key) {
		return storeRankStatsRawDao.getLastKey(key);
	}

	public StoreRankStatsSqlDAO getSqlstoreRankStatsDAO() {
		return sqlstoreRankStatsDAO;
	}

	public void setSqlstoreRankStatsDAO(
			StoreRankStatsSqlDAO sqlstoreRankStatsDAO) {
		this.sqlstoreRankStatsDAO = sqlstoreRankStatsDAO;
	}

	public com.mobilewalla.dao.cassandra.StoreRankStatsDAO getStoreRankStatsRawDao() {
		return storeRankStatsRawDao;
	}

	public void setStoreRankStatsRawDao(
			com.mobilewalla.dao.cassandra.StoreRankStatsDAO storeRankStatsRawDao) {
		this.storeRankStatsRawDao = storeRankStatsRawDao;
	}

}