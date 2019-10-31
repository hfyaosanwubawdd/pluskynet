package com.pluskynet.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.taskdefs.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pluskynet.batch.ReasonUpdate;
import com.pluskynet.domain.DocidAndDoc;
import com.pluskynet.domain.StatsDoc;
import com.pluskynet.otherdomain.Otherdocrule;
import com.pluskynet.util.RuleUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * Latitudeaudit表中的一条规则整理之后 返回一个规则list   
 * @author Administrator
 *
 */
public class DocRule {
	
	private final Logger LOGGER = LoggerFactory.getLogger(DocRule.class);
	public List<Otherdocrule> ruleFormat(JSONArray jsonArray) {
		List<Otherdocrule> list = new ArrayList<Otherdocrule>();
		try {
			JSONObject ruleJson = new JSONObject();
			String startword = null;
			String endword = null;
			String judges = null;
			String spcx = null;
			String doctype = null;
			for (int b = 0; b < jsonArray.size(); b++) {
				Otherdocrule otherdocrule = new Otherdocrule();
				ruleJson = jsonArray.getJSONObject(b);
				judges = ruleJson.getString("judge");
				startword = ruleJson.getString("start");
				endword = ruleJson.getString("end");
				spcx = ruleJson.getString("trialRound");
				doctype = ruleJson.getString("doctype");
				if (list.size() == 0) {//空列表 就添加一个元素    
					otherdocrule.setJudge(ruleJson.getString("judge"));
					otherdocrule.setSpcx(ruleJson.getString("trialRound"));
					otherdocrule.setDoctype(ruleJson.getString("doctype"));
					otherdocrule.setStart(ruleJson.getString("start"));
					otherdocrule.setEnd(ruleJson.getString("end"));
					list.add(otherdocrule);
				} else {//非空列表 
					for (int j = 0; j < list.size(); j++) {
						boolean statrstats = true;
						boolean endstats = true;
						if (list.get(j).getJudge().equals(judges) && list.get(j).getSpcx().equals(spcx)
								&& list.get(j).getDoctype().equals(doctype)) {//judges spcx doctype 有一个匹配
							if (!startword.equals("")) {//
								String[] startwords = list.get(j).getStart().split(";");
								for (int i = 0; i < startwords.length; i++) {
									if (!startwords[i].equals(startword)) {//值相等 匹配到
										statrstats = true;
									} else {
										statrstats = false;
										break;
									}
								}
								if (statrstats) {
									list.get(j).setStart(list.get(j).getStart() + ";" + startword);
								}
							}
							if (!endword.equals("")) {
								String[] endwords = list.get(j).getEnd().split(";");
								for (int i = 0; i < endwords.length; i++) {
									if (!endwords[i].equals(endword)) {
										endstats = true;
									} else {
										endstats = false;
										break;
									}
								}
								if (endstats) {
									list.get(j).setEnd(list.get(j).getEnd() + ";" + endword);
								}
							}
							break;
						} else if (j == list.size() - 1) {//匹配不到 再添加一个元素
							otherdocrule.setJudge(ruleJson.getString("judge"));
							otherdocrule.setSpcx(ruleJson.getString("trialRound"));
							otherdocrule.setDoctype(ruleJson.getString("doctype"));
							otherdocrule.setStart(ruleJson.getString("start"));
							otherdocrule.setEnd(ruleJson.getString("end"));
							list.add(otherdocrule);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public boolean doclist(String docold, List<String> intlist,List<Otherdocrule> list,String spcx,String doctype) {
		String matchStart = ""; // 匹配到的开始词语
		String matchEnd = "";// 匹配到的结束词语
		int start = Integer.valueOf(intlist.get(0));
		int end = Integer.valueOf(intlist.get(1));
		int before_index = 0;
		String olddoc = docold;
		JSONObject ruleJson = null;
		String leftdoc = null;
		String rightdoc = null;
		String beginIndex1 = null;
		String temp_docold = docold;
		String firstMatcherStart = "";
		Integer firstMatcherIndex = 0;
		Integer firstMatcherIndej = 0;
		boolean flag = true;
		String start_before_str = "";
		int startj = 0; //开始词#前长度
		look: for (int c = 0; c < list.size(); c++) {
			if (!doctype.equals("") && !spcx.equals("")) {
				if (list.get(c).getDoctype().equals(doctype)) {
					if (!list.get(c).getSpcx().equals(spcx)) {
						intlist.add(2, matchStart);
						intlist.add(3, matchEnd);
						return true;	
					}
				}else{
					intlist.add(2, matchStart);
					intlist.add(3, matchEnd);
					return true;
				}
			}
			ruleJson = JSONObject.fromObject(list.get(c));
			String startWord = ruleJson.getString("start");
			String endWord = ruleJson.getString("end");
			String judge = ruleJson.getString("judge");
			String[] startWords = startWord.split(";|；");
			String[] endWords = endWord.split(";|；");
			for (int j = 0; j < startWords.length; j++) {
				String before = null;
//				if (startWords[j].contains("@")) {
//					startWords[j] = RuleUtil.getVariableStart(startWords[j], docold);
//				}
				
				if (startWords[j].contains("#")) {
					String startandends = startWords[j].substring(0, startWords[j].lastIndexOf("#") + 1);
					startWords[j] = startWords[j].substring(startWords[j].lastIndexOf("#") + 1);
					String[] startandend = startandends.split("#");
					for (int k = 0; k < startandend.length; k++) {
						if (docold.contains(startandend[k])) {
							start_before_str = startandend[k];
							startj = docold.substring(0,docold.indexOf(startandend[k]) + startandend[k].length()).length();
							docold = docold.substring(docold.indexOf(startandend[k]) + startandend[k].length());
							break;
						}
					}
				} else {
					start_before_str = "";
					startj = 0;
					docold = olddoc;
				}
				if (startWords[j].contains("^")) {
					before = startWords[j].substring(0, startWords[j].indexOf("^"));
					startWords[j] = startWords[j].substring(startWords[j].indexOf("^") + 1);
				} else {
					before = null;
				}
				Pattern patternstart = startRuleFomat(startWords[j]);
//				System.out.println(startWords[j]+" startWords[j]");
				Matcher matcher = patternstart.matcher(docold);
//				LOGGER.info(docold+" docold");
				if (matcher.find()) {
					beginIndex1 = matcher.group();
//					LOGGER.info(beginIndex1+" beginIndex1");
					matchStart = recursionMatcher(patternstart, beginIndex1,startWords[j]);
					beginIndex1 = matchStart;
//					LOGGER.info(matchStart+" matchStart");
//					LOGGER.info(beginIndex1+" beginIndex1");
					start = docold.indexOf(beginIndex1);
					leftdoc = docold.substring(0, docold.indexOf(beginIndex1) + beginIndex1.length());
					StringBuffer s = new StringBuffer(leftdoc);
					if (before != null) {
						leftdoc = s.reverse().toString();
						start = start + beginIndex1.length() - leftdoc.indexOf(before);
					}
					rightdoc = docold.substring(start);
					if (rightdoc != null && start != -1) {
						String newrightdoc = rightdoc;
						for (int x = 0; x < endWords.length; x++) {
							if (endWords[x].contains("#")) {
								String endandstart = endWords[x].substring(0, endWords[x].lastIndexOf("#") + 1);
								endWords[x] = endWords[x].substring(endWords[x].lastIndexOf("#") + 1);
								String[] endandstarts = endandstart.split("#");
								for (int k = 0; k < endandstarts.length; k++) {
									if (rightdoc.contains(endandstarts[k])) {
										rightdoc = rightdoc.substring(0, rightdoc.indexOf(endandstarts[k]));
										break;
									}
								}
							} else {
								rightdoc = newrightdoc;
							}
							String endbefore = "";
							//防止 endWords[x] 值发生改变     没有这个变量偶尔会出现一个bug
							String endwords = endWords[x];
							if (endWords[x].contains("^")) {
								endbefore = endWords[x].substring(0, endWords[x].indexOf("^"));
								endwords = endWords[x].substring(endWords[x].indexOf("^") + 1);
							}
							Pattern patternend = endRuleFomat(endwords);
							Matcher matcher1 = patternend.matcher(rightdoc);
//							LOGGER.info(rightdoc+" rightdoc");
							if (matcher1.find()) {
								String beginIndex = matcher1.group();
								matchEnd = beginIndex;
								if ( !"".equals(endbefore)) {
									String matchAll = rightdoc.substring(0,rightdoc.indexOf(matchEnd));
									int lastIndexOf = matchAll.lastIndexOf(endbefore);
									int indexOf = rightdoc.indexOf(matchEnd);
									matchEnd = rightdoc.substring(lastIndexOf+1,indexOf+matchEnd.length());
								}
								if (endwords.length() > 0) {
//									LOGGER.info(endWords[x]+" endWords[x]");
									if (judge.equals("之前")) {
										if ( !"".equals(endbefore)) {
											rightdoc = rightdoc.substring(0, rightdoc.indexOf(beginIndex));
											end = start + rightdoc.lastIndexOf(endbefore) + endbefore.length()+ startj;
										} else {
											end = start + rightdoc.indexOf(beginIndex)+ startj;
										}
									} else {
										end = start + rightdoc.indexOf(beginIndex) + beginIndex.length()+ startj;
									}
								} else {
									end = rightdoc.length();
								}
								break look;
							}
							if (end == -1 && flag) {
								firstMatcherStart = matchStart;
								firstMatcherIndex = start;
								firstMatcherIndej = startj;
								flag = false;
							}
						}
					}
				}
				docold = temp_docold;
			}
		}
		if (start > end) {end = -1;}
		
		if (end == -1) {
			intlist.set(0, String.valueOf(firstMatcherIndex + firstMatcherIndej));
			intlist.add(2, firstMatcherStart);
		} else {
			intlist.set(0, String.valueOf(start + startj));
			intlist.add(2, matchStart);
		}
		
		intlist.set(1, String.valueOf(end));
		intlist.add(3, matchEnd);
		intlist.add(4, start_before_str);
		return true;
	}
	
	// 开始规则格式化
	public Pattern startRuleFomat(String startWords) {
		String reg_charset = null;
		String[] start = startWords.split("\\*");
		if (start.length > 1) {
			try {
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
							String substring = start[j].substring(0, start[j].indexOf("）"));
							wordnum = Integer.valueOf(substring);
							start[j] = start[j].substring(start[j].indexOf("）") + 1);
						}
					}
					if (reg_charset == null) {
						reg_charset = start[j];
					} else {
						reg_charset = reg_charset + "([\u4e00-\u9fa5_×Ｘa-zA-Z0-9_|\\pP]{0," + wordnum + "})" + start[j];
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				reg_charset = reg_charset + "([\u4e00-\u9fa5_×Ｘa-zA-Z0-9_|\\pP]{0,50})" + start[start.length-1];
			}

		} else {
			if (startWords.startsWith("）") || startWords.startsWith(")")) {
				reg_charset = "["+startWords.substring(0,1)+"]"+startWords.substring(1,startWords.length());
			}else {
				reg_charset = startWords;
			}
		}
		Pattern pattern = Pattern.compile(reg_charset);
		return pattern;
	}

	// 结束规则格式化
	public Pattern endRuleFomat(String endWords) {
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
					reg_charset = reg_charset + "([\u4e00-\u9fa5_×Ｘa-zA-Z0-9_|\\pP]{0," + wordnum + "})" + end[j];
				}
			} else {
				reg_charset = end[j];
			}
		}
		Pattern pattern = Pattern.compile(reg_charset,Pattern.DOTALL);
		return pattern;
	}
	
	public String recursionMatcher(Pattern patternstart,String str,String start) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		String newStr = str.substring(1,str.length());
		Matcher matcher = patternstart.matcher(newStr);
		if (matcher.find()) {
			str = matcher.group();
			recursionMatcher(patternstart, str,start);
		}
		return str;
	}
	
}
