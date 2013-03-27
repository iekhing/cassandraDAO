package com.mobilewalla.domain;

import java.util.ArrayList;
import java.util.List;

public class Application {

	private String key;
	private Long applicationId;
	private String applicationPlatform;
	private Integer applicationType;
	private String country;
	private List<Snapshot> snapshots = new ArrayList<Snapshot>();
	public void addSnapshots(List<Snapshot> snapshot){
		snapshots.addAll(snapshot);
	}
	
	
	public Application(String key, Long applicationId, String applicationPlatform,
			Integer applicationType, String country, List<Snapshot> snapshot) {
		super();
		this.applicationId = applicationId;
		this.applicationPlatform = applicationPlatform;
		this.applicationType = applicationType;
		this.country = country;
		this.snapshots = snapshot;
		this.key = key;
	}

	

	public Application(String key, Long applicationId, String applicationPlatform,
			String country) {
		super();
		this.applicationId = applicationId;
		this.applicationPlatform = applicationPlatform;
		this.country = country;
		this.key = key;
	}
	
	public Long getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}
	public String getApplicationPlatform() {
		return applicationPlatform;
	}
	public void setApplicationPlatform(String applicationPlatform) {
		this.applicationPlatform = applicationPlatform;
	}
	public Integer getApplicationType() {
		return applicationType;
	}
	public void setApplicationType(Integer applicationType) {
		this.applicationType = applicationType;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public List<Snapshot> getSnapshot() {
		return snapshots;
	}
	public void setSnapshot(List<Snapshot> snapshot) {
		this.snapshots = snapshot;
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}

	

}
