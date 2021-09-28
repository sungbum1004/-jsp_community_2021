package com.jsb.exam.exam2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Article {
	private int id;
	private String regDate;
	private String updateDate;
	private int boardId;
	private int memberId;
	private String title;
	private String body;
	
	private String extra__writerName;
	private boolean extra__actorCanModify;
	private boolean extra__actorCanDelete;
	
	public String getTitleForPrint() {
		return title;
	}
	
	public String getBodySummaryForPrint() {
		return body;
	}
	
	public String getBodySummaryForPrintNlToBr() {
		return body.replaceAll("\n", "<br>");
	}
	
	public String getWriterProfileImgUri() {
		return "https://i.pravatar.cc/200?img=" + (memberId % 1000 + 1);
	}
}