package com.pluskynet.domain;

import java.io.Serializable;
import java.util.Date;


/**
* @author HF
* @version 创建时间：2019年9月29日 下午3:18:29
* 类说明
*/
public class Viewhis implements java.io.Serializable{

	private Integer id;
	private String state;
	private String batchno;
	private String rule;
	private String reserved;
	private Integer ruleid;
	private Integer sampleid;
	public Viewhis() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Viewhis(Integer id, String state, String batchno, String rule, String reserved, Integer ruleid,
			Integer sampleid) {
		super();
		this.id = id;
		this.state = state;
		this.batchno = batchno;
		this.rule = rule;
		this.reserved = reserved;
		this.ruleid = ruleid;
		this.sampleid = sampleid;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getBatchno() {
		return batchno;
	}
	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public String getReserved() {
		return reserved;
	}
	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
	public Integer getRuleid() {
		return ruleid;
	}
	public void setRuleid(Integer ruleid) {
		this.ruleid = ruleid;
	}
	public Integer getSampleid() {
		return sampleid;
	}
	public void setSampleid(Integer sampleid) {
		this.sampleid = sampleid;
	}
	@Override
	public String toString() {
		return "Viewhis [id=" + id + ", state=" + state + ", batchno=" + batchno + ", rule=" + rule + ", reserved="
				+ reserved + ", ruleid=" + ruleid + ", sampleid=" + sampleid + "]";
	}
	
}
