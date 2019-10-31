package com.pluskynet.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.pluskynet.action.LatitudeAction;
import com.pluskynet.action.LatitudeauditAction;
import com.pluskynet.dao.BatchdataDao;
import com.pluskynet.dao.CauseDao;
import com.pluskynet.dao.DocSectionAndRuleDao;
import com.pluskynet.dao.DocidandruleidDao;
import com.pluskynet.dao.LatitudeKeyDao;
import com.pluskynet.dao.LatitudenumDao;
import com.pluskynet.dao.LatitudetimeDao;
import com.pluskynet.dao.LatitudewordDao;
import com.pluskynet.domain.Batchdata;
import com.pluskynet.domain.Cause;
import com.pluskynet.domain.Docidandruleid;
import com.pluskynet.domain.Docsectionandrule;
import com.pluskynet.domain.Docsectionandrule01;
import com.pluskynet.domain.Latitude;
import com.pluskynet.domain.Latitudeaudit;
import com.pluskynet.domain.LatitudedocKey;
import com.pluskynet.domain.LatitudedocTime;
import com.pluskynet.domain.LatitudedocWord;
import com.pluskynet.otherdomain.Otherrule;
import com.pluskynet.util.HttpRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OtherRule extends Thread {
	static ClassPathXmlApplicationContext resource = null;
	static BatchdataDao batchdataDao = null;
	// 创建一个静态钥匙
	static Object ob = "aa";// 值是任意的
	static ThreadPoolExecutor executor = null;
	static List<Latitudeaudit> Lalist = null;
	static LatitudeauditAction latitudeauditAction = null;
	int state = -1;// 0 增量跑批， -1 循环跑批
	boolean nextrun = true;

	public OtherRule(String name) {
		super(name);// 给线程起名字
	}

	public String main(int batchstats,boolean nextruns) {
		// System.gc();
		nextrun = nextruns;
		resource = new ClassPathXmlApplicationContext("applicationContext.xml");
		latitudeauditAction = (LatitudeauditAction) resource.getBean("latitudeauditAction");
		Lalist = latitudeauditAction.getLatitude(1);// 获取已审批过的规则  0段落 1其他
		if (Lalist.size() == 0) {
			System.out.println("无规则");
			return "无规则";
		}
		if (batchstats == -1) {
			state = Integer.valueOf(Lalist.get(0).getBatchstats());
		} else {
			state = 0;
		}
		return null;
	}

	public void run() {
		CauseDao causeDao = (CauseDao) resource.getBean("causeDao");
		batchdataDao = (BatchdataDao) resource.getBean("batchdataDao");
		LatitudeAction latitudeAction = (LatitudeAction) resource.getBean("latitudeAction");
		DocSectionAndRuleDao docSectionAndRuleDao = (DocSectionAndRuleDao) resource.getBean("docSectionAndRuleDao");
		LatitudeKeyDao latitudeKeyDao = (LatitudeKeyDao) resource.getBean("latitudeKeyDao");
		LatitudewordDao latitudewordDao = (LatitudewordDao) resource.getBean("latitudeWordDao");
		LatitudetimeDao latitudetimeDao = (LatitudetimeDao) resource.getBean("latitudeTimeDao");
		DocidandruleidDao docidandruleidDao = (DocidandruleidDao) resource.getBean("docidandruleidDao");
		LatitudenumDao latitudenumDao = (LatitudenumDao) resource.getBean("latitudenumDao");
		List<Cause> Causelists = causeDao.getArticleList(1);// 获取表名,0:民事 1:刑事
		List<Docsectionandrule01> docsectionandrulelist = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		for (int i = 0; i < Causelists.size(); i++) {
			do {
				int rows = 2000;
				synchronized (ob) {
					System.out.println("线程名称：" + getName() + "开始取数据;" + df.format(new Date()) + "");
					//文书列表
					docsectionandrulelist = docSectionAndRuleDao.listdoc("docsectionandrule", rows, 1);
//					docsectionandrulelist = docSectionAndRuleDao.listdoc(Causelists.get(i).getDoctable(), rows, state);
					System.out.println("线程名称：" + getName() + "结束取数据;" + df.format(new Date()) + "");
				}
				if (docsectionandrulelist.size() == 0) {
					System.out.println(Causelists.get(i).getDoctable() + "表无数据！！！");
					continue;
				}
				OtherRuleSave otherRuleSave[] = new OtherRuleSave[Lalist.size()];
				for (int j = 0; j < Lalist.size(); j++) {
					latitudeAction.setLatitudeId(Lalist.get(j).getLatitudeid());
					Latitude latitude = latitudeAction.getLatitudes();
					if (null == latitude) {
						continue;
					}
					List<Otherrule> list = ruleFormat(latitude.getRule(), latitude.getRuletype());// 规则整理
					otherRuleSave[j] = new OtherRuleSave();
					otherRuleSave[j].save(list, docsectionandrulelist, latitude, Lalist.get(j).getLatitudename(),
							Lalist.get(j).getLatitudeid(), latitudeKeyDao, batchdataDao, docidandruleidDao);
					otherRuleSave[j].setName("线程名称:" + getName() + "," + "规则线程：" + i + j);
					System.out.println(otherRuleSave[j].getName());
					otherRuleSave[j].start();
				}
				for (int j1 = 0; j1 < otherRuleSave.length; j1++) {
					try {
						if (null != otherRuleSave[j1]) {
							otherRuleSave[j1].join();
							System.out.println("线程名称:" + getName() + "," + otherRuleSave[j1] + "结束");
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} while (docsectionandrulelist.size() > 0);

			if (i == Causelists.size() - 1) {
				if (nextrun) {//跑完之后判断  为true继续 false结束跑批
					for (int j = 0; j < Lalist.size(); j++) {
						if (state == 3) {//根据state值设置 文书状态
							Lalist.get(j).setBatchstats("5");
						} else {
							Lalist.get(j).setBatchstats("3");
						}
						Lalist.get(j).setStats("3");
						if (j == Lalist.size() - 1) {
							latitudeauditAction.updatebatchestats(Lalist);
							/*HttpRequest httpRequest = new HttpRequest();
							httpRequest.sendPost(
									"http://39.104.183.189:8081/pluskynet/LatitudeauditAction!updatebatchestats.action",
									"latitudeaudit=" + Lalist);*/
							latitudenumDao.countlat(1);
							System.out.println("统计完成");
						}
					}
				}
			}
		}
		return;
	}

	public static List<Otherrule> ruleFormat(String rule, int ruletype) {
		JSONArray jsonArray = JSONArray.fromObject(rule);
		List<Otherrule> list = new ArrayList<Otherrule>();
		for (int i = 0; i < jsonArray.size(); i++) {
			Otherrule otherrule = new Otherrule();
			JSONObject jsonObject = JSONObject.fromObject(jsonArray.get(i));
			if (ruletype == 1) {
				otherrule.setSectionname(jsonObject.getString("sectionname"));
				if (jsonObject.containsKey("cond")) {
					otherrule.setCond(jsonObject.getString("cond"));
				}
				if (jsonObject.containsKey("contains")) {
					otherrule.setContains(jsonObject.getString("contains"));
				}
				if (jsonObject.containsKey("nocond")) {
					otherrule.setNocond(jsonObject.getString("nocond"));
				}
				if (jsonObject.containsKey("notcon")) {
					otherrule.setNotcon(jsonObject.getString("notcon"));
				}
				list.add(otherrule);
			} else if (ruletype == 2) {
				otherrule.setSectionname(jsonObject.getString("sectionname"));
				otherrule.setStart(jsonObject.getString("beginText"));
				otherrule.setEnd(jsonObject.getString("endText"));
				list.add(otherrule);
			} else if (ruletype == 3) {
				otherrule.setSectionname(jsonObject.getString("sectionname"));
				otherrule.setNum(jsonObject.getInt("num"));
				otherrule.setTimeformat(jsonObject.getInt("timeformat"));
				list.add(otherrule);
			}
		}
		return list;
	}
}
