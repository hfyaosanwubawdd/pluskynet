package com.pluskynet.service.impl;

import com.pluskynet.service.BaseService;
import com.pluskynet.util.JDBCPoolUtil;

/**
* @author HF
* @version 创建时间：2019年11月6日 下午2:24:50
* 类说明
*/
public class SampleRandLaServiceImpl implements BaseService {

	@Override
	public void deleBySampleid(Integer sampleid) {
		JDBCPoolUtil.executeSql("delete from sample_rand_la where sample_id = "+sampleid);
	}

}
