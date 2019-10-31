package com.pluskynet.batch;
import java.util.HashMap;
import java.util.Map;

/**
* @author HF
* @version 创建时间：2019年5月5日 下午3:27:40
* 类说明
*/
public class BatchCounter {

	public static Map<String,Map<String,Object>> COUNTER_MAP = new HashMap<String,Map<String,Object>>();
	static {
		COUNTER_MAP.put("admin",null);
		COUNTER_MAP.put("liangjiankun",null);
		COUNTER_MAP.put("liuyingze",null);
		COUNTER_MAP.put("suyangping",null);
		COUNTER_MAP.put("marong",null);
		COUNTER_MAP.put("wangwei",null);
		COUNTER_MAP.put("aixia",null);
		COUNTER_MAP.put("ceshi",null);
		COUNTER_MAP.put("jijichang",null);
		COUNTER_MAP.put("hefei",null);
	}
	
}
