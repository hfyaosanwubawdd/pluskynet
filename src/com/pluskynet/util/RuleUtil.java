package com.pluskynet.util;

import org.jsoup.helper.StringUtil;

/**
* @author HF
* @version 创建时间：2019年8月9日 下午1:57:09
* 类说明
*/
public class RuleUtil {

	public static String getVariableStart(String start,String docold) {
		String resultStr = start;
		try {
			String substring = start.substring(0,start.indexOf("@"));
			String startSuffix = start.substring(start.indexOf("@")+1,start.length());
			int indexOf = docold.indexOf(substring);
			String docNew = "";
			if (indexOf >= 0) {
				docNew = docold.substring(indexOf,docold.length());
			}
			if (!StringUtil.isBlank(docNew)) {
				int index = docNew.indexOf("，") > docNew.indexOf("。") ? docNew.indexOf("。") : docNew.indexOf("，");
				start = docNew.substring(substring.length(),index);
				int indexOf2 = start.indexOf("：");	
				if (indexOf2 > 0 ) {
					start = start.substring(indexOf2+1,start.length());
				}
				String startJC = start+"（以下简称";
				int indexOf3 = docNew.indexOf(startJC);
				if (indexOf3 > 0) {
					docNew = docNew.substring(indexOf3+startJC.length(),docNew.length());
					int indexOf5 = docNew.indexOf("）");
//					int indexOf4 = docNew.indexOf("）");
//					int index = indexOf5 > indexOf4 ? indexOf4 : indexOf5;
					start = docNew.substring(0,indexOf5);
				}
				int indexOf4 = start.indexOf("（");
				if (indexOf4 > 0) {
					start = start.substring(0,indexOf4);
				}
				if (!StringUtil.isBlank(start)) {
					resultStr =start+startSuffix;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultStr.replace("@", "");
	}
	public static void main(String[] args) {
		String start = "第三人：@*(10)称：";
		String substring = start.substring(0,start.indexOf("@"));
		System.out.println(substring);
		String startSuffix = start.substring(start.indexOf("@")+1,start.length());
		System.out.println(startSuffix);
	}
}
