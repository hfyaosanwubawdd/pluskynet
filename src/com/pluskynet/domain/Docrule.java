package com.pluskynet.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Docrule entity. @author MyEclipse Persistence Tools
 */

public class Docrule implements java.io.Serializable {

	// Fields

	private Integer ruleid;
	private String sectionname;
	private String rule;
	private Integer fid;
	private String reserved;
	private String rulestate;
	private Integer type;//0民事 1刑事
	
	// Constructors

	/** default constructor */
	public Docrule() {
	}

	/** full constructor */
	
	// Property accessors

	public Integer getRuleid() {
		return this.ruleid;
	}

	public Docrule(Integer ruleid, String sectionname, String rule, Integer fid, String reserved, String rulestate,
			Integer type) {
		super();
		this.ruleid = ruleid;
		this.sectionname = sectionname;
		this.rule = rule;
		this.fid = fid;
		this.reserved = reserved;
		this.rulestate = rulestate;
		this.type = type;
	}

	public void setRuleid(Integer ruleid) {
		this.ruleid = ruleid;
	}

	public String getSectionname() {
		return this.sectionname;
	}

	public void setSectionname(String sectionname) {
		this.sectionname = sectionname;
	}

	public String getRule() {
		return this.rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public Integer getFid() {
		return this.fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public String getReserved() {
		return this.reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getRulestate() {
		return rulestate;
	}

	public void setRulestate(String rulestate) {
		this.rulestate = rulestate;
	}

	@Override
	public String toString() {
		return "Docrule [ruleid=" + ruleid + ", sectionname=" + sectionname + ", rule=" + rule + ", fid=" + fid
				+ ", reserved=" + reserved + ", rulestate=" + rulestate + ", type=" + type + "]";
	}
	
	
}