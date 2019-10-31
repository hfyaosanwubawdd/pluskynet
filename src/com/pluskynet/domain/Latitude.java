package com.pluskynet.domain;

import java.sql.Timestamp;
import java.util.List;

/**
 * Latitude entity. @author MyEclipse Persistence Tools
 */

public class Latitude extends PreParent implements java.io.Serializable {

	// Fields

	private Integer latitudeid;
	private String latitudetype;
	private Integer latitudefid;
	private Integer type;
	private String latitudename;
	private Integer ruletype;
	private String rule;
	private Integer num;
	private String createrole;
	private String createruser;
	private String rulestate;
	private String stats;
	private Timestamp creatertime;
	private String reserved;
	private String creatorName;
	private List<Viewhis> viewList;
	private List<Integer> sampleList;
	
	// Constructors

	/** default constructor */
	public Latitude() {
	}

	/** minimal constructor */
	public Latitude(Integer latitudefid, String latitudename) {
		this.latitudefid = latitudefid;
		this.latitudename = latitudename;
	}

	/** full constructor */

	// Property accessors

	public Integer getLatitudeid() {
		return this.latitudeid;
	}

	public String getRulestate() {
		return rulestate;
	}

	public void setRulestate(String rulestate) {
		this.rulestate = rulestate;
	}

	public Latitude(Integer latitudeid, String latitudetype, Integer latitudefid, Integer type, String latitudename,
			Integer ruletype, String rule, Integer num, String createrole, String createruser, String rulestate,
			String stats, Timestamp creatertime, String reserved, String creatorName, List<Viewhis> viewList,
			List<Integer> sampleList) {
		super();
		this.latitudeid = latitudeid;
		this.latitudetype = latitudetype;
		this.latitudefid = latitudefid;
		this.type = type;
		this.latitudename = latitudename;
		this.ruletype = ruletype;
		this.rule = rule;
		this.num = num;
		this.createrole = createrole;
		this.createruser = createruser;
		this.rulestate = rulestate;
		this.stats = stats;
		this.creatertime = creatertime;
		this.reserved = reserved;
		this.creatorName = creatorName;
		this.viewList = viewList;
		this.sampleList = sampleList;
	}

	public void setLatitudeid(Integer latitudeid) {
		this.latitudeid = latitudeid;
	}

	public String getLatitudetype() {
		return this.latitudetype;
	}

	public void setLatitudetype(String latitudetype) {
		this.latitudetype = latitudetype;
	}

	public Integer getLatitudefid() {
		return this.latitudefid;
	}

	public void setLatitudefid(Integer latitudefid) {
		this.latitudefid = latitudefid;
	}

	public String getLatitudename() {
		return this.latitudename;
	}

	public void setLatitudename(String latitudename) {
		this.latitudename = latitudename;
	}

	public Integer getRuletype() {
		return this.ruletype;
	}

	public void setRuletype(Integer ruletype) {
		this.ruletype = ruletype;
	}

	public String getRule() {
		return this.rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public Integer getNum() {
		return this.num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getCreaterole() {
		return this.createrole;
	}

	public void setCreaterole(String createrole) {
		this.createrole = createrole;
	}

	public String getCreateruser() {
		return this.createruser;
	}

	public void setCreateruser(String createruser) {
		this.createruser = createruser;
	}

	public String getStats() {
		return this.stats;
	}

	public void setStats(String stats) {
		this.stats = stats;
	}

	public Timestamp getCreatertime() {
		return this.creatertime;
	}

	public void setCreatertime(Timestamp creatertime) {
		this.creatertime = creatertime;
	}

	public String getReserved() {
		return this.reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}

	public String getCreatorName() {
		return this.creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<Viewhis> getViewList() {
		return viewList;
	}

	public void setViewList(List<Viewhis> viewList) {
		this.viewList = viewList;
	}
	
	public List<Integer> getSampleList() {
		return sampleList;
	}
	public void setSampleList(List<Integer> sampleList) {
		this.sampleList = sampleList;
	}

	@Override
	public String toString() {
		return "Latitude [latitudeid=" + latitudeid + ", latitudetype=" + latitudetype + ", latitudefid=" + latitudefid
				+ ", type=" + type + ", latitudename=" + latitudename + ", ruletype=" + ruletype + ", rule=" + rule
				+ ", num=" + num + ", createrole=" + createrole + ", createruser=" + createruser + ", rulestate="
				+ rulestate + ", stats=" + stats + ", creatertime=" + creatertime + ", reserved=" + reserved
				+ ", creatorName=" + creatorName + ", viewList=" + viewList + ", sampleList=" + sampleList + "]";
	}

	
}