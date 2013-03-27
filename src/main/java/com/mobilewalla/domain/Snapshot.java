package com.mobilewalla.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Snapshot {
	private  Date snapshopTime; 
	private List <Rank> ranks = new ArrayList<Rank>();
	
	public void addRank(List<Rank> rank){
		ranks.addAll(rank);
	}
	
	public Snapshot(Date snapshopTime) {
		super();
		this.snapshopTime = snapshopTime;
	}

	public Snapshot(Date snapshopTime, List<Rank> ranks) {
		super();
		this.snapshopTime = snapshopTime;
		this.ranks = ranks;
	}
	public Date getSnapshopTime() {
		return snapshopTime;
	}
	public void setSnapshopTime(Date snapshopTime) {
		this.snapshopTime = snapshopTime;
	}
	public List<Rank> getRanks() {
		return ranks;
	}
	public void setRanks(List<Rank> ranks) {
		this.ranks = ranks;
	}
	
	
	
}
