package com.pluskynet.service.impl;
import java.util.ArrayList;
import java.util.List;

import com.pluskynet.batch.thread.SamplesThread;
import com.pluskynet.dao.CauseDao;
import com.pluskynet.dao.DocSectionAndRuleDao;
import com.pluskynet.dao.SampleDao;
import com.pluskynet.domain.Article01;
import com.pluskynet.domain.Cause;
import com.pluskynet.domain.Docsectionandrule01;
import com.pluskynet.domain.Sample;
import com.pluskynet.domain.User;
import com.pluskynet.service.SampleService;
import com.pluskynet.util.JDBCPoolUtil;
import com.pluskynet.util.ThreadPoolSingleton;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SampleServiceImpl implements SampleService{
	private CauseDao causeDao;

	public void setCauseDao(CauseDao causeDao) {
		this.causeDao = causeDao;
	}
	private SampleDao sampleDao;

	public void setSampleDao(SampleDao sampleDao) {
		this.sampleDao = sampleDao;
	}
	
	private DocSectionAndRuleDao docSectionAndRuleDao;
	public void setDocSectionAndRuleDao(DocSectionAndRuleDao docSectionAndRuleDao) {
		this.docSectionAndRuleDao = docSectionAndRuleDao;
	}


	@Override
	public void random(Sample sample,User user,int type) {
		List<Docsectionandrule01> doclist = new ArrayList<Docsectionandrule01>();
		List<Article01> list = new ArrayList<Article01>();
		if (sample.getRule()==null || sample.getRule().equals("[]")) {
			return;
		}
		JSONArray jsonArray = JSONArray.fromObject(sample.getRule());
		String sectionname = "";
		String doctype = "判决书";
		for (int i = 0; i < jsonArray.size(); i++) {
			List<Docsectionandrule01> doclists = new ArrayList<Docsectionandrule01>();
			List<Article01> articleyl = new ArrayList<Article01>();
			JSONObject jsonObject = new JSONObject().fromObject(jsonArray.get(i));
			String year = jsonObject.getString("year");
			int count = jsonObject.getInt("count");
			String trialRound = jsonObject.getString("trialRound");
			
//			sectionname = jsonObject.getString("sectionname");
			Cause cause = new Cause();
//			cause.setCausename(jsonObject.getString("causet"));
			Cause table = causeDao.selectCause(cause);//根据案由名称
			String latitudename = jsonObject.getString("latitudename");
			
			int latitudeid =-1;
			if (!latitudename.equals("")) {
				latitudeid = Integer.valueOf(latitudename);
			}
			if (jsonObject.has("sectionname")) {
				if (!jsonObject.getString("sectionname").equals("")) {
					if (i==0) {
						sampleDao.deleteDoc(user);
					}
					doclists = docSectionAndRuleDao.getDocsectionList(table,year,count,trialRound,doctype,Integer.valueOf(sectionname),latitudeid);
					sampleDao.saveDoc(doclists,user);
				}else{
					if (i==0) {
						sampleDao.delete(user);
					}
					articleyl = sampleDao.getListArticle(table.getCausetable(),year,Integer.valueOf(count),trialRound,doctype,user);
					sampleDao.save(articleyl, user);
				}
			}else{
			articleyl = sampleDao.getListArticle(table.getCausetable(),year,Integer.valueOf(count),trialRound,doctype,user);
			sampleDao.save(articleyl, user);
			}
		}
		sampleDao.saverule(sample,user,type);
	}

	@Override
	public List<Sample> select(User user,int type) {
		List<Sample> sample = sampleDao.select(user,type);	
		return sample;
	}

	@Override
	public void deleteSample(Sample sample) {
		//作为被观察者 处理自己的业务
		deleBySampleid(sample.getId());
		//异步通知观察者们各自处理自己的业务
		ThreadPoolSingleton.getinstance().executeThread(new SamplesThread(sample.getId()));;
	}
	
	/**
	 * 新的样本抽取
	 */
	public void randomNew(Sample sample,User user,int type) {
		sampleDao.saverule(sample,user,type);
	}


	@Override
	public void deleBySampleid(Integer sampleid) {
		JDBCPoolUtil.executeSql("delete from sample where id = "+sampleid);
	}
}
