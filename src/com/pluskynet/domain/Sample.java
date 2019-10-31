package com.pluskynet.domain;

/**
 * Sample entity. @author MyEclipse Persistence Tools
 */

public class Sample implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer type;
	private String rule;
	private String belonguser;
	private Integer belongid;
	private String reserved;
	private String state;
	
	// Constructors

	/** default constructor */
	public Sample() {
	}

	/** minimal constructor */
	public Sample(String rule, String belonguser, Integer belongid) {
		this.rule = rule;
		this.belonguser = belonguser;
		this.belongid = belongid;
	}

	/** full constructor */
	public Sample(String rule, String belonguser, Integer belongid, String reserved) {
		this.rule = rule;
		this.belonguser = belonguser;
		this.belongid = belongid;
		this.reserved = reserved;
	}

	
	
	// Property accessors

	public Sample(Integer id, Integer type, String rule, String belonguser, Integer belongid, String reserved,
			String state) {
		super();
		this.id = id;
		this.type = type;
		this.rule = rule;
		this.belonguser = belonguser;
		this.belongid = belongid;
		this.reserved = reserved;
		this.state = state;
	}


	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRule() {
		return this.rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getBelonguser() {
		return this.belonguser;
	}

	public void setBelonguser(String belonguser) {
		this.belonguser = belonguser;
	}

	public Integer getBelongid() {
		return this.belongid;
	}

	public void setBelongid(Integer belongid) {
		this.belongid = belongid;
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

	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "Sample [id=" + id + ", type=" + type + ", rule=" + rule + ", belonguser=" + belonguser + ", belongid="
				+ belongid + ", reserved=" + reserved + ", state=" + state + "]";
	}

	
	
}