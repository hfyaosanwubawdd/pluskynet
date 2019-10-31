package com.pluskynet.otherdomain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pluskynet.domain.Viewhis;

public class TreeDocrule {
	private Integer ruleid;
	private Integer fid;
	private String sectionname;
	private String rulestate;
	private List<TreeDocrule> children ;
	public Integer getRuleid() {
		return ruleid;
	}
	public void setRuleid(Integer ruleid) {
		this.ruleid = ruleid;
	}
	public Integer getFid() {
		return fid;
	}
	public void setFid(Integer fid) {
		this.fid = fid;
	}
	public String getSectionname() {
		return sectionname;
	}
	public void setSectionname(String sectionname) {
		this.sectionname = sectionname;
	}
	
	public List<TreeDocrule> getChildren() {
		return children;
	}
	public void setChildren(List<TreeDocrule> children) {
		this.children = children;
	}
	public String getRulestate() {
		return rulestate;
	}
	public void setRulestate(String rulestate) {
		this.rulestate = rulestate;
	}
	@Override
	public String toString() {
		return "TreeDocrule [ruleid=" + ruleid + ", fid=" + fid + ", sectionname=" + sectionname + ", rulestate="
				+ rulestate + ", children=" + children + "]";
	}
	
}
