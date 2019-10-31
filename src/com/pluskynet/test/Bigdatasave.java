package com.pluskynet.test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import com.pluskynet.action.DocsectionandruleAction;
import com.pluskynet.dao.ArticleDao;
import com.pluskynet.dao.BatchdataDao;
import com.pluskynet.dao.DocidandruleidDao;
import com.pluskynet.domain.Article01;
import com.pluskynet.domain.Batchdata;
import com.pluskynet.domain.Docidandruleid;
import com.pluskynet.domain.Docsectionandrule;
import com.pluskynet.otherdomain.Otherdocrule;
import com.pluskynet.rule.DocRule;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Bigdatasave extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(Bigdatasave.class);
	private List<Article01> articleList;//文书列表
	private ArticleDao articleDao;
	private BatchdataDao batchdataDao;//以挖掘内容表DAO
	private DocsectionandruleAction docruleaction;
	private DocidandruleidDao docidandruleidDao;
	private String doctable;
	private List<Otherdocrule> lists;//整理之后的规则
	private String causename;
	private String causetable;
	private int ruleid;
	private String latitudename;
	volatile int a = 1;

	/*
	 * public Bigdatasave(String name){ super(name); }
	 */
	public void run() {
		String ruleString = JSONArray.fromObject(lists).toString();
//		List<Article01> docList;
//		docList = articleList;//文书列表
		JSONArray jsonArray = JSONArray.fromObject(lists);
		JSONObject ruleJson = null;
		String title = "";
		String spcx = "";
		String doctype = "";
		String docid = "";
		for (int i1 = 0; i1 < articleList.size(); i1++) {
//			for (int i1 = 0; i1 < docList.size(); i1++) {
			JSONObject jsonObject = new JSONObject().fromObject(articleList.get(i1).getDecodeData());
//			JSONObject jsonObject = new JSONObject().fromObject(docList.get(i1).getDecodeData());
			JSONObject jsonObject2 = jsonObject.getJSONObject("htmlData");
			JSONObject jsonObject3 = jsonObject.getJSONObject("caseinfo");
			 title = jsonObject2.getString("Title");
			spcx = jsonObject3.getString("审判程序");
			doctype = articleList.get(i1).getDoctype();
			docid = articleList.get(i1).getDocId();
//			String title = jsonObject2.getString("Title");
//			String spcx = jsonObject3.getString("审判程序");
//			String doctype = articleList.get(i1).getDoctype();
//			String docid = articleList.get(i1).getDocId();
//			String doctype = docList.get(i1).getDoctype();
//			String docid = docList.get(i1).getDocId();
			String docold = getTextFromHtml(jsonObject2.getString("Html"));//去除各种js html 之后的正文
			List<String> intlist = new ArrayList<String>();
			intlist.add(0, "-1");
			intlist.add(1, "-1");
			DocRule docRule = new DocRule();
			docRule.doclist(docold, intlist, lists, spcx, doctype);
			
			int start = Integer.valueOf(intlist.get(0));
			int end = Integer.valueOf(intlist.get(1));
			String Startword = intlist.get(2);// 匹配到的开始词语
			String Endword = intlist.get(3); // 匹配到的结束词语
			String docnew = null;
			Docsectionandrule docsectionandrule = new Docsectionandrule();
			Batchdata batchdata = new Batchdata();
			docsectionandrule.setRuleid(ruleid);//规则id
			docsectionandrule.setSectionname(latitudename);//文书名称
			docsectionandrule.setDocumentsid(articleList.get(i1).getDocId());//文书id
			docsectionandrule.setTitle(articleList.get(i1).getTitle());//标题
//			docsectionandrule.setDocumentsid(docList.get(i1).getDocId());//文书id
//			docsectionandrule.setTitle(docList.get(i1).getTitle());//标题
			batchdata.setCause(causename);
			batchdata.setDocumentid(articleList.get(i1).getDocId());
//			batchdata.setDocumentid(docList.get(i1).getDocId());
			batchdata.setEndword(Endword);
			batchdata.setRuleid(ruleid);
			batchdata.setStartword(Startword);
			Docidandruleid docidandruleid = new Docidandruleid(articleList.get(i1).getDocId(), ruleid, 0);
//			Docidandruleid docidandruleid = new Docidandruleid(docList.get(i1).getDocId(), ruleid, 0);
			if (end != -1) {
				if (end == 0) {
					docnew = docold.substring(start, docold.length());
					docsectionandrule.setSectiontext(docnew);//段落内容
					batchdataDao.save(batchdata);
					docruleaction.save(docsectionandrule, doctable);
					docidandruleidDao.save(docidandruleid);
					LOGGER.info(currentThread().getName()+"---->"+docid+" save");
					continue;
				} else {
					try {
						docnew = docold.substring(start, end);
						docsectionandrule.setSectiontext(docnew);
					} catch (Exception e) {
						System.out.println(currentThread().getName()+"---->"+docold.length()+":"+start+"-"+end+" 文书编号：" + docid + ",规则id：__________" + ruleid);
					}
					/*
					 * docsectionandrule.setRuleid(ruleid);
					 * docsectionandrule.setSectionname(latitudename);
					 * docsectionandrule.setSectiontext(docnew);
					 * docsectionandrule.setDocumentsid(docList.get(i1).getDocId
					 * ());
					 * docsectionandrule.setTitle(docList.get(i1).getTitle());
					 * batchdata.setCause(causename);
					 * batchdata.setDocumentid(docList.get(i1).getDocId());
					 * batchdata.setEndword(Endword);
					 * batchdata.setRuleid(ruleid);
					 * batchdata.setStartword(Startword);
					 */
					batchdataDao.save(batchdata);
					docruleaction.save(docsectionandrule, doctable);
					docidandruleidDao.save(docidandruleid);
					/*HttpRequest httpRequest = new HttpRequest();
					httpRequest.sendPost("http://39.104.183.189:8081/pluskynet/BatchdataAction!save.action",
							batchdata.toString());
					httpRequest.sendPost("http://39.104.183.189:8081/pluskynet/DocsectionandruleAction!save.action",
							docsectionandrule.toString()+"&"+doctable);
					httpRequest.sendPost("http://39.104.183.189:8081/pluskynet/DocidandruleidAction!save.action",
							docsectionandrule.toString());*/
					continue;
				}
			} else {//没有匹配到
				batchdataDao.delete(batchdata);//删除已挖掘内容表记录
				docruleaction.delete(docsectionandrule, doctable);//段落预览表
				docidandruleidDao.delete(docidandruleid);//已跑批对应表
				/*HttpRequest httpRequest = new HttpRequest();
				httpRequest.sendPost("http://39.104.183.189:8081/pluskynet/BatchdataAction!delete.action",
						batchdata.toString());
				httpRequest.sendPost("http://39.104.183.189:8081/pluskynet/DocsectionandruleAction!delete.action",
						docsectionandrule.toString()+"&"+doctable);
				httpRequest.sendPost("http://39.104.183.189:8081/pluskynet/DocidandruleidAction!delete.action",
						docsectionandrule.toString());*/
			}
			jsonObject.clear();
			jsonObject2.clear();
			jsonObject3.clear();
		}
	}

	public boolean save(List<Article01> articleList, ArticleDao articleDao, BatchdataDao batchdataDao,
			DocsectionandruleAction docrule, DocidandruleidDao docidandruleidDao, String doctable,
			List<Otherdocrule> lists, String causename, String causetable, int ruleid, String latitudename) {
		this.articleList = articleList;
		this.articleDao = articleDao;
		this.batchdataDao = batchdataDao;
		this.docruleaction = docrule;
		this.docidandruleidDao = docidandruleidDao;
		this.doctable = doctable;
		this.lists = lists;
		this.causename = causename;
		this.ruleid = ruleid;
		this.causetable = causetable;
		this.latitudename = latitudename;
		String a = getName();
		return true;
	}

	// 开始规则正则化
	public static Pattern startRuleFomat(String startWords) {
		String reg_charset = null;
		String[] start = startWords.split("\\*");
		if (start.length > 1) {
			for (int j = 0; j < start.length; j++) {
				if (reg_charset == null) {
					reg_charset = start[j];
				} else {
					reg_charset = reg_charset + "([\u4e00-\u9fa5_×Ｘa-zA-Z0-9_|\\pP]{0,50})" + start[j];
				}
			}
		} else {
			reg_charset = startWords;
		}
		Pattern pattern = Pattern.compile(reg_charset);
		return pattern;
	}

	// 结束规则正则化
	public static Pattern endRuleFomat(String endWords) {
		String reg_charset = null;
		String[] end = endWords.split("\\*");
		for (int j = 0; j < end.length; j++) {
			if (end.length > 1) {
				if (reg_charset == null) {
					reg_charset = end[j];
				} else {
					reg_charset = reg_charset + "([\u4e00-\u9fa5_×Ｘa-zA-Z0-9_|\\pP]{0,50})" + end[j];
				}
			} else {
				reg_charset = end[j];
			}
		}
		Pattern pattern = Pattern.compile(reg_charset);
		return pattern;
	}

	/*
	 * 规则格式化
	 */
	public static List<Otherdocrule> ruleformat(String rule) {
		List<Otherdocrule> list = new ArrayList<Otherdocrule>();
		JSONArray jsonArray = new JSONArray();
		JSONObject ruleJson = new JSONObject();
		jsonArray = JSONArray.fromObject(rule);
		String startword = null;
		String endword = null;
		String judges = null;
		String causet = null;
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
			if (list.size() == 0) {
				otherdocrule.setJudge(ruleJson.getString("judge"));
				otherdocrule.setSpcx(ruleJson.getString("trialRound"));
				otherdocrule.setDoctype(ruleJson.getString("doctype"));
				otherdocrule.setStart(ruleJson.getString("start"));
				otherdocrule.setEnd(ruleJson.getString("end"));
				list.add(otherdocrule);
			} else {
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).getJudge().equals(judges) && list.get(j).getSpcx().equals(spcx)
							&& list.get(j).getDoctype().equals(doctype)) {
						list.get(j).setStart(list.get(j).getStart() + ";" + startword);
						list.get(j).setEnd(list.get(j).getEnd() + ";" + endword);
						break;
					} else if (j == list.size() - 1) {
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
		return list;
	}

	public static List<Article01> breakup(List<Article01> articleList, String rule)
			throws InvocationTargetException, IllegalAccessException {
		String trialRound = null;// 获取审判程序
		String doctype = null;// 获取文书类型
		String title = null;// 获取标题
		String spcx = null;// 获取审判类型
		JSONArray jsonArray = JSONArray.fromObject(rule);// 格式化规则
		List<Article01> list = new ArrayList<Article01>();
		for (int i = articleList.size() - 1; i >= 0; i--) {
			Article01 article01 = new Article01();
			BeanUtils.copyProperties(articleList.get(i), article01);
			for (int j = 0; j < jsonArray.size(); j++) {
				JSONObject js = new JSONObject();
				js = jsonArray.getJSONObject(j);
				trialRound = js.getString("spcx");
				doctype = js.getString("doctype");
				JSONObject jsonObject = new JSONObject().fromObject(article01.getDecodeData());
				JSONObject jsonObject2 = jsonObject.getJSONObject("htmlData");
				JSONObject jsonObject3 = jsonObject.getJSONObject("caseinfo");
				title = jsonObject2.getString("Title");
				spcx = jsonObject3.getString("审判程序");
				if (title.indexOf(doctype) != -1 && spcx.equals(trialRound)) {
					article01.setDecodeData(jsonObject2.getString("Html"));
					list.add(article01);
					break;

				}
			}
		}
		return list;

	}

	private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
	private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
	private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
	private static final String regEx_space = "\\s*|\t|\r|\n";// 定义空格回车换行符

	public static String delHTMLTag(String htmlStr) {
		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签

		Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
		Matcher m_space = p_space.matcher(htmlStr);
		htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
		htmlStr = htmlStr.replace(" ", "");
		htmlStr = htmlStr.replace("  ", "");
		htmlStr = htmlStr.replaceAll("　", "");
		return htmlStr.replaceAll("　　 ", ""); // 返回文本字符串
	}

	public static String getTextFromHtml(String htmlStr) {
		htmlStr = delHTMLTag(htmlStr);
		htmlStr = htmlStr.replaceAll("&nbsp;", "");
		// htmlStr = htmlStr.substring(0, htmlStr.indexOf("。")+1);
		return htmlStr;
	}

}
