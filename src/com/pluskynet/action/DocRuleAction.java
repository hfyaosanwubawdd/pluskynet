package com.pluskynet.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jasper.tagplugins.jstl.core.If;
import org.apache.log4j.Logger;
import com.pluskynet.batch.ArticleInfo;
import com.pluskynet.batch.BatchConstant;
import com.pluskynet.batch.CivilJudgmentThread;
import com.pluskynet.batch.CreateDictData;
import com.pluskynet.batch.Demo6TExportAll;
import com.pluskynet.batch.DocIdAndYear;
import com.pluskynet.batch.LatitudeDocKey;
import com.pluskynet.batch.ReasonUpdate;
import com.pluskynet.batch.SectionBatchPenal;
import com.pluskynet.batch.SectionBatchPenal5364;
import com.pluskynet.batch.SupplyLevel;
import com.pluskynet.batch.ViewhisBatch;
import com.pluskynet.data.KeywordAppellor;
import com.pluskynet.data.thread.SectionBatchThread;
import com.pluskynet.domain.Cause;
import com.pluskynet.domain.Docrule;
import com.pluskynet.domain.Docsectionandrule;
import com.pluskynet.domain.Preview;
import com.pluskynet.domain.StatsDoc;
import com.pluskynet.domain.User;
import com.pluskynet.domain.Viewhis;
import com.pluskynet.otherdomain.TreeDocrule;
import com.pluskynet.service.DocRuleService;
import com.pluskynet.service.PreviewService;
import com.pluskynet.test.Bigdatatest;
import com.pluskynet.util.BaseAction;
import javassist.expr.NewArray;
import net.sf.json.JSONObject;

@SuppressWarnings("all")
public class DocRuleAction extends BaseAction {
	private Logger LOGGER = Logger.getLogger(DocRuleAction.class);
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	// 接收前端传过来的值
	private Integer ruleid;

	public Integer getRuleid() {
		return ruleid;
	}

	public void setRuleid(Integer ruleid) {
		this.ruleid = ruleid;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	private String sectionName;
	private String rule;
	private JSONObject queryresult;
	private List<Map> sectionlist;

	public List<Map> getSectionlist() {
		return sectionlist;
	}

	public void setSectionlist(List<Map> sectionlist) {
		this.sectionlist = sectionlist;
	}

	public JSONObject getQueryresult() {
		return queryresult;
	}

	public void setQueryresult(JSONObject queryresult) {
		this.queryresult = queryresult;
	}

	private Docrule docrule = new Docrule();

	@Override
	public Docrule getModel() {
		return docrule;
	}

	private DocRuleService docRuleService;

	public void setDocRuleService(DocRuleService docRuleService) {
		this.docRuleService = docRuleService;
	}

	private List<Map> q_result;

	public List<Map> getQ_result() {
		return q_result;
	}

	public void setQ_result(List<Map> q_result) {
		this.q_result = q_result;
	}

	Map map = new HashMap();
	private PreviewService previewService;

	public PreviewService getPreviewService() {
		return previewService;
	}

	public void setPreviewService(PreviewService previewService) {
		this.previewService = previewService;
	}

	private String causeo;
	private String causet;
	private String spcx;
	private String doctype;

	public String getCauseo() {
		return causeo;
	}

	public void setCauseo(String causeo) {
		this.causeo = causeo;
	}

	public String getCauset() {
		return causet;
	}

	public void setCauset(String causet) {
		this.causet = causet;
	}

	public String getSpcx() {
		return spcx;
	}

	public void setSpcx(String spcx) {
		this.spcx = spcx;
	}

	public String getDoctype() {
		return doctype;
	}
	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}
	
	
	

	/*
	 * 保存段落名称
	 */
	public void save() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		LOGGER.info("民事段落规则 ---> save() --> "+user.getUsername());
		Map map = new HashMap();
		String msg = null;
		if (docrule.getSectionname() == null) {
			docrule.setSectionname(sectionName);
			map = docRuleService.save(docrule,0);
		} else {
			map = docRuleService.save(docrule,0);
		}
		outJsonByMsg(map, "成功");
	}

	public void savePenal() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		LOGGER.info("刑事段落规则 --> savePenal() --> "+user.getUsername());
		Map map = new HashMap();
		String msg = null;
		if (docrule.getSectionname() == null) {
			docrule.setSectionname(sectionName);
			map = docRuleService.save(docrule,1);
		} else {
			map = docRuleService.save(docrule,1);
		}
		outJsonByMsg(map, "成功");
	}
	/*
	 * 修改名称或规则
	 */
	public void update() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		String msg = null;
		if (docrule.getRuleid() == null) {
			msg = "失败";
			LOGGER.info("修改名称或规则失败"+user.getUsername());
		} else {
			if (docrule.getSectionname() == null) {
				docrule.setSectionname(sectionName);
			}
			msg = docRuleService.update(docrule);
			LOGGER.info("修改名称或规则成功"+user.getUsername());
		}
		outJsonByMsg(msg);
	}

	/*
	 * 修改名称
	 */
	public void updatesecname() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		String msg = null;
		if (docrule.getRuleid() == null) {
			msg = "失败";
		} else {
			if (docrule.getSectionname() == null) {
				docrule.setSectionname(sectionName);
			}
			msg = docRuleService.updatesecname(docrule);
		}
		outJsonByMsg(msg);
	}

	/*
	 * 查询规则段落列表
	 */
	public void getDocSectionList() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		List<TreeDocrule> list = docRuleService.getDcoSectionList(0);
		outJsonByMsg(list, "成功");
	}

	public void getDocSectionListPenal() {
		 User user = isLogined();
		 if (user == null) {
		 outJsonByMsg("未登录");
		 return;
		 }
		List<TreeDocrule> list = docRuleService.getDcoSectionList(1);
		outJsonByMsg(list, "成功");
	}
	
	private Integer  requestType;
	public Integer getRequestType() {
		return requestType;
	}
	public void setRequestType(Integer requestType) {
		this.requestType = requestType;
	}
	/*
	 * 根据ID查询规则详细
	 */
	public void getDocSection() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		String msg = "失败";
		Integer userid = user.getUserid();
		if (docrule.getRuleid() == null) {
			outJsonByMsg(msg);
		} else {
			map = docRuleService.getDcoSection(docrule);
			ViewhisBatch viewhisBatch = new ViewhisBatch();
			Integer type = viewhisBatch.getTypeByRuleid(docrule.getRuleid());
			System.out.println(type+" docrule.getType()");
			requestType =  1;
			if (null == type || 0 == type ) {
				requestType = 0;
			}
			try {
				map.put("viewList",viewhisBatch.getViewhisListByRuleid(docrule.getRuleid()));
				msg = "成功";
				if (null == requestType || requestType != 0) {//民事
					map.put("sampleList", viewhisBatch.getSampleids(12,userid));
				}else {
					map.put("sampleList", viewhisBatch.getSampleids(11,userid));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			outJsonByMsg(map, msg);
		}
	}

	/*
	 * 根据段落名称查询
	 */
	public void getSecNameShow() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		String msg = "成功";
		if (docrule.getSectionname() == null) {
			docrule.setSectionname(sectionName);
			List<Map> list = docRuleService.getSecNameShow(docrule.getSectionname());
			outJsonByMsg(list, msg);
		} else {
			List<Map> list = docRuleService.getSecNameShow(docrule.getSectionname());
			msg = "成功";
			outJsonByMsg(list, msg);
		}
	}

	/*
	 * 按照条件查询规则
	 */
	public void getRuleShow() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		String msg = "失败";
		if (docrule.getRuleid() == null) {
			outJsonByMsg(msg);
		} else {
			List<Docrule> list = docRuleService.getRuleShow(docrule.getRuleid(), causeo, causet, spcx, doctype);
			msg = "成功";
			outJsonByMsg(list.get(0), msg);
		}
	}

	/*
	 * 循环跑批开始
	 */
	public void Docrun() {
		String[] spcxs = KeywordAppellor.DATA_SPCXS;
		String[] years = KeywordAppellor.DATA_YEARS;
		for (int i = 0; i < spcxs.length; i++) {
			String spcx = spcxs[i];
			KeywordAppellor.initPreviewList(spcx);
			for (int j = 0; j < years.length; j++) {
				String data = years[j];
				Thread t = new Thread() {
					@Override
					public void run() {
						SectionBatchThread secThread = new SectionBatchThread();
						secThread.sectionBatchExecuteThread(data, spcx);
					}
				};
				t.start();
			}
		}
		outJsonByMsg("成功");
	}

	/*
	 * 增量跑批开始
	 */
	public void newDocrun() {
	}
	public void newDocrun1() {
		LatitudeDocKey.addKeyCountDownLath();
	}
}
