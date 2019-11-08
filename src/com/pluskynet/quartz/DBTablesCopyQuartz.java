package com.pluskynet.quartz;

import org.apache.log4j.Logger;

import com.pluskynet.util.JDBCPoolUtil;
import com.pluskynet.util.ThreadPoolSingleton;

/**
* @author HF
* @version 创建时间：2019年11月8日 上午10:05:37
* 类说明
*/
public class DBTablesCopyQuartz {

	private Logger LOGGER = Logger.getLogger(DBTablesCopyQuartz.class);
	public void executeQuartzTask() {
		ThreadPoolSingleton instance = ThreadPoolSingleton.getinstance();
		String[] tables = {"cause","docrule","latitude","latitudeaudit","t_mapping","t_para_cri","t_para_cri_grp","t_para_grp","t_para_one","t_para_vector"};
		for (int i = 0; i < tables.length; i++) {
			int num = i;
			instance.executeThread(new Runnable() {
				@Override
				public void run() {
					LOGGER.info("copy table start --> "+ tables[num] );
					JDBCPoolUtil.executeSql("truncate z_c_"+tables[num]);
					JDBCPoolUtil.executeSql("insert into z_c_"+tables[num]+" SELECT *  FROM "+tables[num]);
					LOGGER.info("copy table end --> "+ tables[num] );
				}
			});
		}
	}
}
