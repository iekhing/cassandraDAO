package com.mobilewalla.domain;

public class Rank {
	private Long category;
	private Long rank;
	private String feedType;
	private String mediaType;

	public Rank(Long category, Long rank, String feedType, String mediaType) {
		super();
		this.category = category;
		this.rank = rank;
		this.feedType = feedType;
		this.mediaType = mediaType;
	}

	public Long getCategory() {
		return category;
	}

	public void setCategory(Long category) {
		this.category = category;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

}
