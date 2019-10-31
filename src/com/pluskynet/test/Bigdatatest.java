package com.pluskynet.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pluskynet.action.DocsectionandruleAction;
import com.pluskynet.action.LatitudeauditAction;
import com.pluskynet.action.LatitudenumAction;
import com.pluskynet.action.PreviewAction;
import com.pluskynet.dao.ArticleDao;
import com.pluskynet.dao.BatchdataDao;
import com.pluskynet.dao.CauseDao;
import com.pluskynet.dao.DocidandruleidDao;
import com.pluskynet.dao.LatitudenumDao;
import com.pluskynet.domain.Article01;
import com.pluskynet.domain.Batchdata;
import com.pluskynet.domain.Cause;
import com.pluskynet.domain.Docidandruleid;
import com.pluskynet.domain.Docsectionandrule;
import com.pluskynet.domain.Latitudeaudit;
import com.pluskynet.domain.Latitudenum;
import com.pluskynet.otherdomain.Otherdocrule;
import com.pluskynet.rule.DocRule;
import com.pluskynet.util.HttpRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * 已分好表的的数据进行分段规则跑批
 */
@SuppressWarnings("all")
public class Bigdatatest extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(Bigdatatest.class);
	static ClassPathXmlApplicationContext resource = null;
	static ArticleDao articleDao = null;
	static DocsectionandruleAction docrule = null;
	static BatchdataDao batchdataDao;
	static LatitudenumDao latitudenumDao;
	// 创建一个静态钥匙
	Object ob = "aa";// 值是任意的
	volatile private int a = 0;
	static ThreadPoolExecutor executor = null;

	static List<Latitudeaudit> Lalist = null;
	static LatitudeauditAction latitudeauditAction;
	int allorre = -1;// 0：新增跑批，（ 3：二次跑批 ,5:再次跑批）
	public Bigdatatest(String name) {
		super(name);// 给线程起名字
	}
	
	/**
	 * Lalist 				 获取已审批过的规则    维度类型为段落审核状态为已审批 Latitudeaudit 0段落 1其他
	 * allorre 				 获取已审批规则的文书状态   根据此状态跑批
	 * @param batchstats 	-1 循环跑批  其他增量跑批
	 */
	public void main(int batchstats) {
		resource = new ClassPathXmlApplicationContext("applicationContext.xml");
		latitudeauditAction = (LatitudeauditAction) resource.getBean("latitudeauditAction");
		latitudenumDao = (LatitudenumDao) resource.getBean("latitudenumDao");
		Lalist = latitudeauditAction.getLatitude(0);
		if (batchstats == -1 ) {
			allorre = Integer.valueOf(Lalist.get(0).getBatchstats());
		}else{
			allorre = 0;//latitudeaudit.batchstats 文书状态
		}
	}

	public void run() {
		articleDao = (ArticleDao) resource.getBean("articleDao");
		PreviewAction previewAction = (PreviewAction) resource.getBean("previewAction");
		docrule = (DocsectionandruleAction) resource.getBean("docsectionandruleAction");
		batchdataDao = (BatchdataDao) resource.getBean("batchdataDao");
		DocidandruleidDao docidandruleidDao = (DocidandruleidDao) resource.getBean("docidandruleidDao");
		
		if (Lalist.size() > 0) {
			CauseDao causeDao = (CauseDao) resource.getBean("causeDao");
			String doctable = null;
			List<Cause> list = null;
			list = getValue();//读取csv文件
			if (list.size() == 0 || list == null) {
				list = causeDao.getArticleList(1);// 去cause表获取 文书表名causetable 段落表doctable,0:民事 1:刑事
			}
			LOGGER.info(currentThread().getName()+"---->"+list.get(0).getCausename());
			List<Article01> articleList = null;
			for (int i = 0; i < list.size(); i++) {
				doctable = list.get(i).getDoctable();// 段落表名
				boolean runs = true;
				int rows = 2000;
				while (runs) {
					synchronized (ob) {
						LOGGER.info(currentThread().getName() + "---->" + "线程名称：" + getName());
						articleList = articleDao.getArticle01List(list.get(i).getCausetable(), allorre, rows);// 获取文书列表
					}
					if (articleList.size() == 0) {
						LOGGER.info(currentThread().getName() + "---->" + list.get(i).getCausetable() + "表无数据！！！");
						runs = false;
						continue;
					}
					LOGGER.info(currentThread().getName() + "---->" + list.get(i).getCausetable());
					Bigdatasave bigdatasave[] = new Bigdatasave[Lalist.size()];
					for (int j = 0; j < Lalist.size(); j++) {// 循环已审批规则
						List<Otherdocrule> lists = new ArrayList<Otherdocrule>();
						DocRule docRule = new DocRule();
						lists = docRule.ruleFormat(JSONArray.fromObject(Lalist.get(j).getRule())); // 规则整理
						int ruleid = Lalist.get(j).getLatitudeid();// 维度id
						String latitudename = Lalist.get(j).getLatitudename();// 维度名称
						String startword = null;
						String endword = null;
						String judges = null;
//						List<Article01> articleLists = articleList;
						bigdatasave[j] = new Bigdatasave();
						bigdatasave[j].save(articleList, articleDao, batchdataDao, docrule, docidandruleidDao,
								list.get(i).getDoctable(), lists, list.get(i).getCausename(),
								list.get(i).getCausetable(), ruleid, latitudename);
						bigdatasave[j].setName(currentThread().getName() + "---->" + "线程名称:" + getName() + "," + "规则线程：" + i + j);
						LOGGER.info(currentThread().getName() + "---->" + bigdatasave[j].getName());
						bigdatasave[j].start();
						if (i == list.size() - 1) {// 审核状态
							for (int k = 0; k < Lalist.size(); k++) {
								if (allorre == 3) {
									Lalist.get(k).setBatchstats("5");
								} else {
									Lalist.get(k).setBatchstats("3");
								}
								Lalist.get(k).setStats("3");
							}
						}
					}

					for (int j = 0; j < bigdatasave.length; j++) {
						try {
							LOGGER.info(currentThread().getName() + "---->" + bigdatasave.length + " now " + j);
							bigdatasave[j].join();
							LOGGER.info(currentThread().getName() + "---->" + "线程名称:" + getName() + "," + bigdatasave[j]+ "结束");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			latitudeauditAction.updatebatchestats(Lalist);//更新维度审批表
		} else {
			LOGGER.info("无规则");
		}
		latitudenumDao.countlat(0);
	}

	public static List<Cause> getValue() {
		List<Cause> list = new ArrayList<Cause>();
		File directory = new File("");// 设定为当前文件夹
		try {
			File f = new File(directory.getAbsolutePath());
			System.out.println(f.getParent());// 获取绝对路径
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream("C:/users/administrator/bigdata.csv"), "UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {
				String[] lines = line.split(";");
				for (int i = 0; i < lines.length; i++) {
					String[] tiao = lines[i].split(",");
					for (int b = 0; b < 1; b++) {
						Cause cause = new Cause();
						cause.setCausename(tiao[0].substring(1, tiao[0].length()));
						cause.setCausetable(tiao[1]);
						cause.setDoctable(tiao[2]);
						list.add(cause);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
