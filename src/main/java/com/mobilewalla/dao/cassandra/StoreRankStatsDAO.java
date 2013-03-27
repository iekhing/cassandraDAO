package com.mobilewalla.dao.cassandra;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.beans.OrderedSuperRows;
import me.prettyprint.hector.api.beans.SuperRow;
import me.prettyprint.hector.api.beans.SuperSlice;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSuperSlicesQuery;
import me.prettyprint.hector.api.query.SuperSliceQuery;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobilewalla.domain.Application;
import com.mobilewalla.domain.Rank;
import com.mobilewalla.domain.Snapshot;

public class StoreRankStatsDAO {

	private static final Logger log = LoggerFactory
			.getLogger(StoreRankStatsDAO.class);

	private static final String dateFormat = "yyyy-MM-dd-HH";// 2013-03-21-06
	private static final SimpleDateFormat dateFormatted = new SimpleDateFormat(
			dateFormat);

	private String columnFamilyName;
	private Keyspace keyspace;

	public Map<Date, Map<String, Integer>> getAllColumnByKey(final String key) {
		SuperSliceQuery<String, String, String, String> rangeSlicesQuery = HFactory
				.createSuperSliceQuery(keyspace, StringSerializer.get(),
						StringSerializer.get(), StringSerializer.get(),
						StringSerializer.get());

		rangeSlicesQuery.setKey(key);
		rangeSlicesQuery.setColumnFamily(columnFamilyName);
		rangeSlicesQuery.setRange(null, null, false, 500);
		QueryResult<SuperSlice<String, String, String>> result = rangeSlicesQuery
				.execute();
		SuperSlice<String, String, String> row = result.get();
		List<HSuperColumn<String, String, String>> superColumns = row
				.getSuperColumns();
		Map<Date, Map<String, Integer>> superColumnData = new LinkedHashMap<Date, Map<String, Integer>>();
		log.debug("super coloums list size {} ", superColumns.size());
		for (HSuperColumn<String, String, String> superColumn : superColumns) {
			log.error("super coloums name {} ", superColumn.getName());
			List<HColumn<String, String>> columns = superColumn.getColumns();
			log.error(" coloums size {} ", columns.size());
			Map<String, Integer> columnData = new LinkedHashMap<String, Integer>();

			for (HColumn<String, String> column : columns) {
				columnData.put(column.getName(),
						NumberUtils.toInt(StringUtils.trim(column.getValue())));
				log.debug("super column name {} column name  {} value {} ",
						superColumn.getName(), column.getName(),
						NumberUtils.toInt(StringUtils.trim(column.getValue())));
			}

			try {
				log.debug("super column name formatted {}",
						dateFormatted.parse(superColumn.getName()));
				superColumnData.put(dateFormatted.parse(superColumn.getName()),
						columnData);
			} catch (ParseException e) {

			}
		}
		return superColumnData;

	}

	
	public List<Application> getAll(String key, Integer pageSize) {
		RangeSuperSlicesQuery<String, String, String, String> rangeSlicesQuery = HFactory
				.createRangeSuperSlicesQuery(keyspace, StringSerializer.get(),
						StringSerializer.get(), StringSerializer.get(),
						StringSerializer.get());

		rangeSlicesQuery.setKeys(key, null);
		rangeSlicesQuery.setColumnFamily(columnFamilyName);
		rangeSlicesQuery.setRange(null, null, false, 500);
		rangeSlicesQuery.setRowCount(pageSize+1);
		QueryResult<OrderedSuperRows<String, String, String, String>> result = rangeSlicesQuery
				.execute();
		OrderedSuperRows<String, String, String, String> orderRow = result
				.get();
		List<SuperRow<String, String, String, String>> rows = orderRow
				.getList();
		List<Application> applications = new ArrayList<Application>();
		for (SuperRow<String, String, String, String> row : rows) {
			log.debug("key name {} ", row.getKey());
			String[] id = parseApplicationId(row.getKey());
			Application app = new Application(	row.getKey(),
												NumberUtils.toLong(StringUtils.trim(id[1])), 
												id[0], 
												id[2]);
			SuperSlice<String, String, String> superSlice = row.getSuperSlice();
			app.addSnapshots(convertSuperColumnToSnapshot(superSlice.getSuperColumns()));
			applications.add(app);
		} 
		return applications;
	}
	
	
	public String getLastKey(String key) {
		RangeSuperSlicesQuery<String, String, String, String> rangeSlicesQuery = HFactory
				.createRangeSuperSlicesQuery(keyspace, StringSerializer.get(),
						StringSerializer.get(), StringSerializer.get(),
						StringSerializer.get());

		rangeSlicesQuery.setKeys(key, null);
		rangeSlicesQuery.setColumnFamily(columnFamilyName);
		rangeSlicesQuery.setRange(null, null, false, 0);
		rangeSlicesQuery.setRowCount(501);
		QueryResult<OrderedSuperRows<String, String, String, String>> result = rangeSlicesQuery
				.execute();
		OrderedSuperRows<String, String, String, String> orderRow = result.get();
		return orderRow.getList().get(orderRow.getCount()-1).getKey();
	}
	
	private List<Snapshot> convertSuperColumnToSnapshot (List<HSuperColumn<String, String, String>> superColumns){
		List<Snapshot> snapshots = new ArrayList<Snapshot>();
		for (HSuperColumn<String, String, String> superColumn : superColumns) {
			log.debug("super coloums name {} ", superColumn.getName());
			try {
				Snapshot snapshot = new Snapshot(dateFormatted.parse(superColumn
						.getName()));
				snapshot.addRank(convertHColumnstoRank(superColumn.getColumns()));
				snapshots.add(snapshot);
			} catch (ParseException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		return snapshots;
	}
	
	
	private List<Rank> convertHColumnstoRank (List<HColumn<String, String>> columns){
		log.debug(" coloums size {} ", columns.size());
		List<Rank> ranks = new ArrayList<Rank>();
		for (HColumn<String, String> column : columns) {
			log.debug(
					"column name  {} value {} ",
					column.getName(), 
					NumberUtils.toInt(StringUtils.trim(column.getValue())));

			String[] parsedColumnValue = parseApplicationCategory(column
					.getName());
			Rank rank = new Rank
							(NumberUtils.toLong(StringUtils.trim(parsedColumnValue[1])),
							NumberUtils.toLong(StringUtils.trim(column.getValue())), 
							parsedColumnValue[2],
							parsedColumnValue[0]);
			if (StringUtils.equals(rank.getMediaType(), "t")){
				continue;
			}
			ranks.add(rank);
		}
		return ranks;
	}

	private String[] parseApplicationCategory(String value) {
		log.debug(" parse category {}", value);
		String[] media = StringUtils.split(value, "-");
		String[] applicationCategory = StringUtils.split(media[1], "#");
		String feedType = null;
		if (applicationCategory.length == 2){
			feedType = applicationCategory[1];
		}
		
		return new String[] { media[0], 
							  applicationCategory[0],
							  feedType};
	}

	private String[] parseApplicationId(String id) {
		return StringUtils.split(id, "-");
	}

	public String getColumnFamilyName() {
		return columnFamilyName;
	}

	public void setColumnFamilyName(String columnFamilyName) {
		this.columnFamilyName = columnFamilyName;
	}

	public Keyspace getKeyspace() {
		return keyspace;
	}

	public void setKeyspace(Keyspace keyspace) {
		this.keyspace = keyspace;
	}

}
