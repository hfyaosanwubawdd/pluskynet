package com.pluskynet.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.apache.log4j.Logger;
import com.pluskynet.batch.PreviewBatch;
import com.pluskynet.dao.PreviewDao;
import com.pluskynet.domain.Articleyl;
import com.pluskynet.domain.DocidAndDoc;
import com.pluskynet.domain.Preview;
import com.pluskynet.domain.StatsDoc;
import com.pluskynet.otherdomain.Otherdocrule;
import com.pluskynet.parsing.Parsing;
import com.pluskynet.rule.DocRule;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings("all")
public class PreviewDaoImpl extends HibernateDaoSupport implements PreviewDao {
	private Logger LOGGER = Logger.getLogger(PreviewDaoImpl.class);
	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pluskynet.dao.PreviewDao#getDocList(com.pluskynet.domain.Preview,
	 * java.util.List) 检索符合规则的文书 和 不符合规则的文书列表
	 */
	public List<StatsDoc> getDocList(Preview preview, List<Articleyl> listaArticles) {
		DocRule docRule = new DocRule();
		Parsing parsing = new Parsing();
		// 文书拆分，返回docid和文书主文章信息
		List<DocidAndDoc> docList = new ArrayList<DocidAndDoc>();
		List<DocidAndDoc> docLists = null;
		List<StatsDoc> statsDocs = new ArrayList<StatsDoc>();
		JSONArray jsonArray = new JSONArray();
		JSONObject ruleJson = new JSONObject();
		jsonArray = jsonArray.fromObject(preview.getRule());
		Integer fhnum = 0;
		Integer bfhnum = 0;
		List<Otherdocrule> list = docRule.ruleFormat(jsonArray);
		docLists = parsing.DocList(listaArticles, preview);
		docList.addAll(docLists);
		for (int i = 0; i < docList.size(); i++) {
			StatsDoc statsDoc = new StatsDoc();
			DocidAndDoc docidAndDoc = new DocidAndDoc();
			String docid = docList.get(i).getDocid();
			String docold = docList.get(i).getDoc();
			String doctitle = docList.get(i).getTitle();
			String docnew = null;
			List<String> intlist = new ArrayList<String>();
			intlist.add(0,"-1");
			intlist.add(1,"-1");
			docRule.doclist(docold,intlist,list,"","");
			int start = Integer.valueOf(intlist.get(0));
			int end = Integer.valueOf(intlist.get(1));
			if (end == -1) {
				statsDoc.setStats("不符合");
				docidAndDoc.setDocid(docid);
				docidAndDoc.setTitle(doctitle);
				statsDoc.setDocidAndDoc(docidAndDoc);
				statsDoc.setNum(bfhnum++);
				statsDocs.add(statsDoc);
			} else {
				
				//这里有问题    应该先判断 end == 0 在判断 end != -1
				//不知道是不是什么坑   发现问题再改过来看看吧
				//什么情况啊  上面已经判断 end == -1 了
				if (end != -1) {
					docnew = docold.substring(start, end);
				} else if (end == 0) {
					docnew = docold.substring(start, docold.length());
				}
				
				statsDoc.setStats("符合");
				if (docnew.startsWith("。") || docnew.startsWith("）") ||  docnew.startsWith(")")) {
					docnew = docnew.substring(1,docnew.length());
				}
				docidAndDoc.setDoc(docnew);
				docidAndDoc.setDocid(docid);
				docidAndDoc.setTitle(doctitle);
				statsDoc.setDocidAndDoc(docidAndDoc);
				statsDoc.setNum(fhnum++);
				statsDocs.add(statsDoc);
			}
		}
		return statsDocs;
	}

	// 开始规则格式化
	public Pattern startRuleFomat(String startWords) {
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

	// 结束规则格式化
	public Pattern endRuleFomat(String endWords) {
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
}
