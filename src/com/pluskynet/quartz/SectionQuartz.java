package com.pluskynet.quartz;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pluskynet.batch.thread.SectionQuartzThread;
import com.pluskynet.util.JDBCPoolUtil;
import com.pluskynet.util.ThreadPoolSingleton;

/**
* @author HF
* @version 创建时间：2019年11月1日 上午9:49:18
* 类说明		定时分段
*/
public class SectionQuartz {

	private Logger LOGGER = Logger.getLogger(SectionQuartz.class);
	public void sectionQuartzTaskStart() {
		List<Map<String, Object>> list = JDBCPoolUtil.selectBySql("select ruleid,ruletype,year,lastid from t_quartz_data where state is null and type is null limit 5");
		for (int i = 0; i < list.size(); i++) {
			ThreadPoolSingleton instance = ThreadPoolSingleton.getinstance();
			instance.executeThread(new SectionQuartzThread(list.get(i)));
		}
	}
}
