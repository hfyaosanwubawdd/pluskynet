package com.pluskynet.service.impl;

import com.pluskynet.service.BaseService;
import com.pluskynet.util.JDBCPoolUtil;

/**
* @author HF
* @version 创建时间：2019年11月6日 下午2:26:59
* 类说明
*/
public class ViewHisServiceImpl implements BaseService {

	@Override
	public void deleBySampleid(Integer sampleid) {
		JDBCPoolUtil.executeSql("delete from t_view_his where sampleid = "+sampleid);
	}

}
