package com.mobilewalla.dao.cassandra;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MWRankListStatDAO {

	private static final Logger log = LoggerFactory.getLogger(MWRankListStatDAO.class);

	private String columnFamilyName;
	private Keyspace keyspace;
	private final StringSerializer serializer = StringSerializer.get();

	public String get(final String key, final Long columnName)
			throws HectorException {
		ColumnQuery<String, Long, String> q = HFactory.createColumnQuery(
				keyspace, serializer, LongSerializer.get(), serializer);
		QueryResult<HColumn<Long, String>> r = q.setKey(key)
				.setName(columnName).setColumnFamily(columnFamilyName)
				.execute();
		HColumn<Long, String> c = r.get();
		return c != null ? c.getValue() : null;
	}

	public Map<String, Map<Long, String>> getAllColumnByKey(final String key) throws HectorException {
		RangeSlicesQuery<String, Long, String> rangeSlicesQuery = HFactory
				.createRangeSlicesQuery(keyspace, StringSerializer.get(),
						LongSerializer.get(), StringSerializer.get())
				.setColumnFamily(columnFamilyName)
				.setRange(null, null, false, 500);
		QueryResult<OrderedRows<String, Long, String>> result = rangeSlicesQuery
				.setKeys(key, key).execute();
		return processQueryResul(result);
	}

	public Map<String, Map<Long, String>> getAll() throws HectorException {
		RangeSlicesQuery<String, Long, String> rangeSlicesQuery = HFactory
				.createRangeSlicesQuery(keyspace, StringSerializer.get(),
						LongSerializer.get(), StringSerializer.get())
				.setColumnFamily(columnFamilyName)
				.setRange(null, null, false, 500).setRowCount(101);
		QueryResult<OrderedRows<String, Long, String>> result = rangeSlicesQuery
				.execute();
		return processQueryResul(result);
	}

	public Map<String, Map<Long, String>> getAll(String key)
			throws HectorException {
		RangeSlicesQuery<String, Long, String> rangeSlicesQuery = HFactory
				.createRangeSlicesQuery(keyspace, StringSerializer.get(),
						LongSerializer.get(), StringSerializer.get())
				.setColumnFamily(columnFamilyName)
				.setRange(null, null, false, 500).setRowCount(101);
		QueryResult<OrderedRows<String, Long, String>> result = rangeSlicesQuery
				.setKeys(key, null).execute();
		return processQueryResul(result);

	}

	private Map<String, Map<Long, String>> processQueryResul(
			QueryResult<OrderedRows<String, Long, String>> result) {
		OrderedRows<String, Long, String> rows = result.get();
		Iterator<Row<String, Long, String>> rowsIterator = rows.iterator();
		Map<String, Map<Long, String>> data = new LinkedHashMap<String, Map<Long, String>>();
		while (rowsIterator.hasNext()) {
			Row<String, Long, String> row = rowsIterator.next();
			if (row.getColumnSlice().getColumns().isEmpty()) {
				continue;
			}
			log.debug("key {} ", row.getKey());
			List<HColumn<Long, String>> columnsIter = row.getColumnSlice()
					.getColumns();
			Map<Long, String> columns = new LinkedHashMap<Long, String>();
			for (HColumn<Long, String> column : columnsIter) {
				convertFields(column,columns);
				log.debug("key {}  column name {}  value {} ", row.getKey(),
						column.getName(), column.getValue());
			}
			data.put(row.getKey(), columns);
		}
		return data;
	}

	private Map<Long, String> convertFields(HColumn<Long, String> column, Map<Long, String> colums) {
		colums.put(column.getName(), column.getValue());
		return colums;
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
