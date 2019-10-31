package com.pluskynet.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pluskynet.dao.BatchdataDao;
import com.pluskynet.dao.DocidandruleidDao;
import com.pluskynet.dao.LatitudeKeyDao;
import com.pluskynet.domain.Batchdata;
import com.pluskynet.domain.Docidandruleid;
import com.pluskynet.domain.Docsectionandrule01;
import com.pluskynet.domain.Latitude;
import com.pluskynet.domain.LatitudedocKey;
import com.pluskynet.otherdomain.Otherrule;
import com.pluskynet.util.HttpRequest;
import com.sun.star.lib.uno.environments.remote.IReceiver;

import javassist.expr.NewArray;
import net.sf.json.JSONObject;

public class OtherRuleSave extends Thread {
	private List<Otherrule> list = null;
	private List<Docsectionandrule01> docsectionandrulelist = null;
	private Latitude latitude = null;
	private String latitudename = null;
	private Integer latitudeid = null;
	private LatitudeKeyDao latitudeKeyDao;
	private BatchdataDao batchdataDao;
	private DocidandruleidDao docidandruleidDao;

	public boolean save(List<Otherrule> list, List<Docsectionandrule01> docsectionandrulelist, Latitude latitude,String latitudename,int latitudeid
			,LatitudeKeyDao latitudeKeyDao,BatchdataDao batchdataDao,DocidandruleidDao docidandruleidDao) {
		this.list = list;
		this.docsectionandrulelist = docsectionandrulelist; //文书列表
		this.latitude = latitude; //规则
		this.latitudename = latitudename; //规则名称
		this.latitudeid = latitudeid; //规则id
		this.latitudeKeyDao = latitudeKeyDao;
		this.batchdataDao = batchdataDao;
		this.docidandruleidDao = docidandruleidDao;
//		System.out.println("赋值成功！！！！");
		return true;
	}

	public void run() {
		HttpRequest httpRequest = new HttpRequest();
		for (int i = 0; i < docsectionandrulelist.size(); i++) {
			boolean a = false;
			String documentid = docsectionandrulelist.get(i).getDocumentsid(); //文书id
			String sectionname = docsectionandrulelist.get(i).getSectionname(); //文书段落名称
			int ruleid = docsectionandrulelist.get(i).getRuleid();//文书段落规则id
			String oldsectiontext = docsectionandrulelist.get(i).getSectiontext();//段落内容
			String rulesec;
			look: for (int j = 0; j < list.size(); j++) {//遍历规则
				rulesec = list.get(j).getSectionname();//规则名称
				if (null == rulesec && "[]".equals(rulesec) && "".equals(rulesec)) {
					continue;
				}
				try {//getSectionname  可能是中文也可能是int串
					if (ruleid!=Integer.valueOf(list.get(j).getSectionname())) {
						continue;
					}
				} catch (Exception e) {
					continue;
				}
				JSONObject jsonObject = JSONObject.fromObject(list.get(j));
				if (latitude.getRuletype() == 1) {//刑事案件
					String location = "";
					String contains = jsonObject.getString("contains");
					if (contains.equals("")) {
						a = true;
						location = "0,0;";
					} else {
						if (contains.contains("*")) {
							Pattern containp = startRuleFomat(contains);//开始的规则格式化
							Matcher matcher = containp.matcher(oldsectiontext);
							if (matcher.find()) {
								String beginIndex = matcher.group();
								a = true;
								location =String.valueOf(oldsectiontext.indexOf(beginIndex))+","+beginIndex.length()+";";
							}
						} else if (contains.contains("&")) {
							String[] contain = contains.split("\\&");// 包含
							for (int x = 0; x < contain.length; x++) {
								if (oldsectiontext.contains(contain[x].toString())) {
										a = true;
									if (location.equals("")) {
										location = String.valueOf(oldsectiontext.indexOf(contain[x].toString()))+","+contain[x].toString().length()+";";
									}else {
										location = location + String.valueOf(oldsectiontext.indexOf(contain[x].toString()))+","+contain[x].toString().length()+";";
									}
								} else {
									a = false;
									location = "";
									continue;
								}
							}
						} else {
							if (oldsectiontext.contains(contains)) {
								a = true;
								location = String.valueOf(oldsectiontext.indexOf(contains))+","+contains.length()+";";
							} else {
								a = false;
								continue;
							}
						}
					}
					if (a) {//以上陪配到任意一种
						String[] notcon = jsonObject.getString("notcon").split(";;");
						for (int k = 0; k < notcon.length; k++) {
							if (notcon[k].contains("*")) {
								Pattern containp = endRuleFomat(notcon[k]);
								Matcher matcher = containp.matcher(oldsectiontext);
								if (!matcher.find()) {
									a = true;
								} else {
									a = false;
									break;
								}
							} else if (!oldsectiontext.contains(notcon[k])) {
								a = true;
							} else if (notcon[k].equals("")) {
								a = true;
							} else {
								a = false;
								break;
							}
						}
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						LatitudedocKey latitudedocKey = new LatitudedocKey();
						latitudedocKey.setDocumentid(documentid);
						latitudedocKey.setLatitudename(latitudename);
						latitudedocKey.setLatitudeid(latitudeid);
						latitudedocKey.setSectionid(Integer.valueOf(rulesec));
						latitudedocKey.setLocation(location);
						latitudedocKey.setUpdatatime(df.format(new Date()));
						Batchdata batchdata = new Batchdata();
						batchdata.setDocumentid(documentid);
						batchdata.setRuleid(latitudeid);
						batchdata.setContain(contains);
						Docidandruleid docidandruleid = new Docidandruleid(
								documentid,latitudeid,1);
						if (a) {
							latitudeKeyDao.save(latitudedocKey);
//							batchdataDao.save(batchdata);
//							docidandruleidDao.save(docidandruleid);
							/*httpRequest.sendPost("http://39.104.183.189:8080/pluskynet/latitudeKeyAction!save.action", "latitudedockey="+latitudedocKey);
							httpRequest.sendPost("http://39.104.183.189:8080/pluskynet/BatchdataAction!save.action", "batchdata="+batchdata);
							httpRequest.sendPost("http://39.104.183.189:8080/pluskynet/DocidandruleidAction!save.action", "docidandruleid="+docidandruleid);
							*/break look;
						}else {
							latitudeKeyDao.delete(latitudedocKey);
//							batchdataDao.delete(batchdata);
//							docidandruleidDao.delete(docidandruleid);
							/*httpRequest.sendPost("http://39.104.183.189:8080/pluskynet/latitudeKeyAction!delete.action", "latitudedockey="+latitudedocKey);
							httpRequest.sendPost("http://39.104.183.189:8080/pluskynet/BatchdataAction!delete.action", "batchdata="+batchdata);
							httpRequest.sendPost("http://39.104.183.189:8080/pluskynet/DocidandruleidAction!delete.action", "docidandruleid="+docidandruleid);
				*/
							break look;
						}
					}
				}else if (latitude.getRuletype() == 2) {
					
				}else if (latitude.getRuletype() == 3) {
					
				}
			}
		}
	}

	// 开始规则格式化
	public static Pattern startRuleFomat(String startWords) {
		String reg_charset = null;
		String[] start = startWords.split("\\*");
		if (start.length > 1) {
			for (int j = 0; j < start.length; j++) {
				int wordnum = 50;
				for (int i = 0; i < start[j].length(); i++) {
					if (start[j].charAt(i) == '(') {
						start[j] = start[j].substring(start[j].indexOf("(") + 1);
					} else if (start[j].charAt(i) == '（') {
						start[j] = start[j].substring(start[j].indexOf("（") + 1);
					} else if (start[j].charAt(i) == ')') {
						wordnum = Integer.valueOf(start[j].substring(0, start[j].indexOf(")")));
						start[j] = start[j].substring(start[j].indexOf(")") + 1);
					} else if (start[j].charAt(i) == '）') {
						wordnum = Integer.valueOf(start[j].substring(0, start[j].indexOf("）")));
						start[j] = start[j].substring(start[j].indexOf("）") + 1);
					}
				}
				if (reg_charset == null) {
					reg_charset = start[j];
				} else {
					reg_charset = reg_charset + "([\u4e00-\u9fa5_×Ｘa-zA-Z0-9_|\\pP，。？：；‘’！“”—……、]{0," + wordnum + "})" + start[j];
				}
			}
		} else {
			reg_charset = startWords;
		}
		Pattern pattern = Pattern.compile(reg_charset);
		return pattern;
	}

	// 结束规则格式化
	public static Pattern endRuleFomat(String endWords) {
		String reg_charset = null;
		String[] end = endWords.split("\\*");
		for (int j = 0; j < end.length; j++) {
			if (end.length > 1) {
				int wordnum = 50;
				for (int i = 0; i < end[j].length(); i++) {
					if (end[j].charAt(i) == '(') {
						end[j] = end[j].substring(end[j].indexOf("(") + 1);
					} else if (end[j].charAt(i) == '（') {
						end[j] = end[j].substring(end[j].indexOf("（") + 1);
					} else if (end[j].charAt(i) == ')') {
						wordnum = Integer.valueOf(end[j].substring(0, end[j].indexOf(")")));
						end[j] = end[j].substring(end[j].indexOf(")") + 1);
					} else if (end[j].charAt(i) == '）') {
						wordnum = Integer.valueOf(end[j].substring(0, end[j].indexOf("）")));
						end[j] = end[j].substring(end[j].indexOf("）") + 1);
					}
				}
				if (reg_charset == null) {
					reg_charset = end[j];
				} else {
					reg_charset = reg_charset + "([\u4e00-\u9fa5_×Ｘa-zA-Z0-9_|\\pP，。？：；‘’！“”—……、]{0," + wordnum + "})" + end[j];
				}
			} else {
				reg_charset = end[j];
			}
		}
		Pattern pattern = Pattern.compile(reg_charset);
		return pattern;
	}

}
