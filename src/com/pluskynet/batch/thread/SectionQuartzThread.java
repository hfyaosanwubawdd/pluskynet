package com.pluskynet.batch.thread;

import java.util.Map;

import org.apache.log4j.Logger;

/**
* @author HF
* @version 创建时间：2019年11月4日 上午10:20:13
* 类说明
*/
public class SectionQuartzThread implements Runnable{

	private Map<String, Object> map;
	private Integer ruletype;
	private Integer lastid;
	private Integer ruleid;
	public SectionQuartzThread() {
		super();
	}
	public SectionQuartzThread(Map<String, Object> map) {
		super();
		this.map = map;
		this.ruletype = (Integer) map.get("ruletype");
		this.ruleid = (Integer) map.get("ruleid");
		this.lastid = map.get("lastid") == null ? 0 : (int) map.get("lastid");
	}

	private Logger LOGGER = Logger.getLogger(SectionQuartzThread.class);
	
	@Override
	public void run() {
		LOGGER.info("定时任务执行    "+map.toString());
	}

}
