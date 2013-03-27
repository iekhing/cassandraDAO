package com.mobilewalla.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.mobilewalla.domain.Application;
import com.mobilewalla.domain.Rank;
import com.mobilewalla.domain.Snapshot;

public class StoreRankStatsSqlDAO {
	private static final Logger log = LoggerFactory
			.getLogger(StoreRankStatsSqlDAO.class);

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public int insert(Long appid, String country, Long rank, Long categoryId,
			Date crawlingTime, String feedType) {
		return this.jdbcTemplate
				.update("insert into store_rank_stats (appid, country, rank, catid, date,  feedType) values (?, ?, ?, ?, ?, ?)",
						appid, country, rank, categoryId, crawlingTime,
						feedType);

	}

	public int deleteByAppId(Long appid) {
		return this.jdbcTemplate.update(
				"delete from store_rank_stats where appid = ? ", appid);

	}

	public Application getRecord(Long appid, String country, Long rank,
			Long categoryId, Date crawlingTime, String feedType) {
		List<Application> apps = null;
		if (StringUtils.isBlank(feedType)) {
			apps = (List<Application>) this.jdbcTemplate
					.query("select appid, country, rank, catid, date, feedType, free, feedType "
							+ "from store_rank_stats "
							+ "where appid = ? and country = ? and catid=? and date=? ",
							new Object[] { appid, country, categoryId,
									crawlingTime },
							new RowMapper<Application>() {

								public Application mapRow(final ResultSet rs,
										final int rowNum) throws SQLException {
									return mapResultSetToObject(rs);
								}
							});
		} else
			apps = (List<Application>) this.jdbcTemplate
					.query("select appid, country, rank, catid, date, feedType, free, feedType "
							+ "from store_rank_stats "
							+ "where appid = ? and country = ? and catid=? and date=? and feedType = ? ",
							new Object[] { appid, country, categoryId,
									crawlingTime, feedType },
							new RowMapper<Application>() {

								public Application mapRow(final ResultSet rs,
										final int rowNum) throws SQLException {
									
									return mapResultSetToObject(rs);
								}
							});

		if (apps != null && apps.size() != 0) {
			return apps.get(0);
		} else {
			return null;
		}

	}

	private Application mapResultSetToObject(ResultSet rs) throws SQLException {
		Application app = null;
		List<Rank> ranks = new ArrayList<Rank>();
		ranks.add(new Rank(rs.getLong("catid"), rs.getLong("rank"), rs
				.getString("feedType"), "i"));
		List<Snapshot> snapshots = new ArrayList<Snapshot>();
		snapshots.add(new Snapshot(rs.getDate("date"), ranks));
		app = new Application(
				"i-"+rs.getLong("appid")+"-"+rs.getString("country"),
				rs.getLong("appid"), "i", Integer.valueOf(1),
				rs.getString("country"), snapshots);
		return app;
	}
}
