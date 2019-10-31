package com.pluskynet.action;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.pluskynet.batch.BatchCounter;
import com.pluskynet.batch.ViewhisBatch;
import com.pluskynet.domain.Preview;
import com.pluskynet.domain.StatsDoc;
import com.pluskynet.domain.User;
import com.pluskynet.domain.Viewhis;
import com.pluskynet.service.PreviewService;
import com.pluskynet.util.BaseAction;
import java.util.Date;
@SuppressWarnings("all")
public class PreviewAction extends BaseAction{
	private final Logger LOGGER = Logger.getLogger(PreviewAction.class);
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm____ss-");
	
	private List<StatsDoc> doclist;
	public List<StatsDoc> getDoclist() {
		return doclist;
	}
	public void setDoclist(List<StatsDoc> doclist) {
		this.doclist = doclist;
	}
	
	private Preview preview = new Preview();
	@Override
	public Preview getModel() {
		return preview;
	}
	
	private PreviewService previewService;
	public void setPreviewService(PreviewService previewService) {
		this.previewService = previewService;
	}
	
	private String docid;
	public String getDocid() {
		return docid;
	}
	public void setDocid(String docid) {
		this.docid = docid;
	}

	/** 预览历史需要 规则id**/
	private Integer id;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	private Integer sampleid;
	public Integer getSampleid() {
		return sampleid;
	}
	public void setSampleid(Integer sampleid) {
		this.sampleid = sampleid;
	}
	/*
	 * 规则预览
	 */
	public void getDocList() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		String checkId = preview.getCheckId();
		if (preview.getRule() == null || preview.getRule().equals("")) {
			outJsonByMsg("失败");
		} else {
			if (null == sampleid || 0 == sampleid) {
				sampleid = 416;
			}
			String batch_no = sdf.format(new Date())+RandomUtils.nextInt(1000);
			ViewhisBatch viewhis = new ViewhisBatch();
			Viewhis vie = viewhis.saveHis(preview, false, id	,batch_no,sampleid);
			ExecutorService threadPool = Executors.newCachedThreadPool();
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					ViewhisBatch viewhis = new ViewhisBatch();
					List<StatsDoc> list = previewService.getDocListNew(preview, user, checkId,0,sampleid,viewhis.countByBatchno(sampleid, ""),batch_no);
					viewhis.saveViewhis(list,preview,false,batch_no);
				}
			});
			threadPool.shutdown();
			outJsonByMsg(vie, "成功");
		}
	}
	
	/*
	 * 规则预览
	 */
	public void getDocListPenal() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		String checkId = preview.getCheckId();
		if (preview.getRule() == null || preview.getRule().equals("")) {
			outJsonByMsg("失败");
		} else {
			if (null == sampleid || 0 == sampleid) {
				sampleid = 416;
			}
			String batch_no = sdf.format(new Date())+RandomUtils.nextInt(1000);
			ViewhisBatch viewhis = new ViewhisBatch();
			Viewhis vie = viewhis.saveHis(preview, false, id,batch_no,sampleid);
			ExecutorService threadPool = Executors.newCachedThreadPool();
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					ViewhisBatch viewhis = new ViewhisBatch();
					Long count = viewhis.countByBatchno(sampleid, "");
					List<StatsDoc> list = previewService.getDocListNew(preview, user, checkId,1,sampleid,count,batch_no);
					viewhis.saveViewhis(list,preview,false,batch_no);
				}
			});
			threadPool.shutdown();
			outJsonByMsg(vie, "成功");
		}
	}
	public void getDoc(){
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		Map<String, Object> map= previewService.getDocNew(docid,preview.getRule());
		outJsonByMsg(map,"成功");
	}
	
	public void check() {
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		String checkId = preview.getCheckId();
		String username = user.getUsername();
		Map<String, Object> map = BatchCounter.COUNTER_MAP.get(username);
		Map<String, Object> innerMap = new HashMap<String, Object>();
		try {
			if (map == null || map.isEmpty()) {
				innerMap.put("completeState", "完成");
				outJsonByMsg(innerMap,"成功");
			}else {
				if (map.containsKey(checkId)) {
					innerMap = (Map<String, Object>) map.get(checkId);
					if (map.containsKey(checkId+"check")) {
						Map<String, Object> innerMapCheck = (Map<String, Object>) map.get(checkId+"check");
						innerMapCheck.put("check", System.currentTimeMillis());
						BatchCounter.COUNTER_MAP.get(username).put(checkId+"check", innerMapCheck);
					}else {
						innerMap.put("completeState", "完成");
					}
					outJsonByMsg(innerMap,"成功");
					
					if ("完成".equals(innerMap.get("completeState"))) {
						map.clear();
						BatchCounter.COUNTER_MAP.put(username,map);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			innerMap.put("totalCount",9999);
			innerMap.put("completeCount",0L);
			innerMap.put("completeState","完成");
			map.clear();
			BatchCounter.COUNTER_MAP.put(username,map);
			outJsonByMsg(innerMap,"成功");
		}
	}
}
