package com.pluskynet.service.impl;

import com.pluskynet.service.BaseService;
import com.pluskynet.util.JDBCPoolUtil;

/**
* @author HF
* @version 创建时间：2019年11月6日 下午2:22:09
* 类说明
*/
public class SampleRandServiceImpl implements BaseService {

	@Override
	public void deleBySampleid(Integer sampleid) {
		JDBCPoolUtil.executeSql("delete from sample_rand where sample_id = "+sampleid);
	}

}
