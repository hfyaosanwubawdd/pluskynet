package com.pluskynet.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import freemarker.template.utility.StringUtil;

/**
* @author HF
* @version 创建时间：2019年7月2日 上午9:57:43
* 类说明
*/
public class BatchUtils {

	public static String getDoctypeByTitle(String title) {
		String doctype = "";
		if (title.contains("判决书")) {
			doctype = "判决书";
		} else if (title.contains("裁定书")) {
			doctype = "裁定书";
		} else if (title.contains("调解书")) {
			doctype = "调解书";
		} else if (title.contains("决定书")) {
			doctype = "决定书";
		} else if (title.contains("通知书")) {
			doctype = "通知书";
		} else if (title.contains("批复")) {
			doctype = "批复";
		} else if (title.contains("答复")) {
			doctype = "答复";
		} else if (title.contains("函")) {
			doctype = "函";
		} else if (title.contains("令")) {
			doctype = "令";
		}
		return doctype;
	}
	
	public static String getCourtLevel(String court) {
		String level = "基层";
		if ("最高人民法院".equals(court)) {
			level = "最高";
		}else if (court.contains("高级人民法院")) {
			level = "高级";
		}else if (court.contains("中级人民法院") || court.contains("知识产权法院") || court.contains("海事法院") 
				|| "天津铁路运输法院".equals(court) || "福州铁路运输法院".equals(court)) {
			level = "中级";
		}
		return level;
	}
	
	public static boolean isBlank(ResultSet rs) {
		boolean flag = true;
		try {
			rs.last();
			int row = rs.getRow();
			if (row > 0) {
				flag = false;
			}
			rs.beforeFirst();
		} catch (SQLException e) {
			flag = true;
			e.printStackTrace();
		}
		return flag;
	}
	
	public static Map<String,String> docIndexList(String doc) {
		Map<String,String> resultMap = new HashMap<String, String>();
		String mark = "##########";
		doc = doc.replace("><div", ">"+mark+"<div");
		String reg = "<[^>]+>";
		doc = doc.replaceAll(reg, "");
		String[] ary = doc.split(mark);
		StringBuffer buffer = new StringBuffer();
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for(int i = 0 ; i < ary.length; i++) {
			buffer.append(ary[i]);
			indexList.add(buffer.length());
		}
		String result = buffer.toString();
		resultMap.put("indexList",indexList.toString().replace(" ", ""));
		resultMap.put("result",result);
		return resultMap;
	}
	
	
	public static String reasonSplit(String reason) {
		if (StringUtils.isBlank(reason)) {
			return "";
		}
		String[] split = reason.split(",");
		return split[0];
	}
	
	public static String docsectionandruleIndexList(String str) {
		if (StringUtils.isBlank(str)) {
			return "0";
		}
		String index = "";
		str = str.substring(1,str.length()-1);
		String[] split = str.split(",");
		Set<String> indexSet = new TreeSet<String>();
		for (String string : split) {
			indexSet.add(string);
		}
		index = indexSet.toString().replace(" ", "");
		index = index.substring(1,index.length()-1);
		return index;
	}
}
