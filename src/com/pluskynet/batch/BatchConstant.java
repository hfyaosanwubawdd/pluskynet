package com.pluskynet.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pluskynet.dao.CauseDao;
import com.pluskynet.domain.Cause;
import com.pluskynet.util.JDBCPoolUtil;

/**
* @author HF
* @version 创建时间：2019年4月25日 上午10:44:24
* 类说明   不在线上跑也无所谓
*/
public class BatchConstant {
	public static String[] TABLE_NAMES= {"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20",};
	public static String[] DATA = {"2003","2004","2005","2006","2007","2008","2010","2011","2012","2013","2014","2015","2016","2017","2018"};
	public static String[] SPCX = {"一审","二审","再审"};
	public static final String USER="root";
	public static final String PWD="L3fhExzCz1CpaHbc";
	public static final String URL="jdbc:mysql://192.168.1.195:3306/wenshu?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false";
	public static final String DRIVER ="com.mysql.jdbc.Driver";
	
	public static final String USER1="wenshu";
	public static final String PWD1="1qaz@WSXYLT";
	public static final String URL1="jdbc:mysql://192.168.1.222:3306/wenshu?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false";
	public static final String DRIVER1 ="com.mysql.jdbc.Driver";
	
	/**rds**/
	public static final String USER2="wenshu";
	public static final String PWD2 = "1qaz@WSXYLT";
	public static final String URL2="jdbc:mysql://rm-hp3pks3337r99yu25jo.mysql.huhehaote.rds.aliyuncs.com/pluskynet?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false";
	public static final String DRIVER2 ="com.mysql.jdbc.Driver";


	public static List<Map> LALIST_PENAL = new ArrayList<Map>();
	public static List<Map> LALIST_CIVIL = new ArrayList<Map>();
	
	public static Map<Integer,String> LA_ALL = new HashMap<Integer, String>();
	public static void initLaallMap() {
		List<Map<String, Object>> list = JDBCPoolUtil.selectBySql("select latitudeid,latitudename from latitude");
		for (Map<String, Object> map : list) {
			LA_ALL.put((int)map.get("latitudeid"),(String) map.get("latitudename"));
		}
	}
}
