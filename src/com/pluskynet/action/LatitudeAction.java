package com.pluskynet.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.pluskynet.batch.BatchConstant;
import com.pluskynet.batch.SectionBatchPenal;
import com.pluskynet.batch.ViewhisBatch;
import com.pluskynet.domain.Latitude;
import com.pluskynet.domain.StatsDoc;
import com.pluskynet.domain.User;
import com.pluskynet.domain.Viewhis;
import com.pluskynet.otherdomain.Treelatitude;
import com.pluskynet.service.LatitudeService;
import com.pluskynet.util.BaseAction;
import com.pluskynet.util.JSONUtil;

import javassist.expr.NewArray;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings("all")
public class LatitudeAction extends BaseAction {
	private Logger LOGGER = Logger.getLogger(LatitudeAction.class);
	private Latitude latitude = new Latitude();
	@Override
	public Latitude getModel() {
		return latitude;
	}
	
	private LatitudeService latitudeService;
	public void setLatitudeService(LatitudeService latitudeService) {
		this.latitudeService = latitudeService;
	}

	private JSONObject latitu;
	public JSONObject getLatitu() {
		return latitu;
	}
	public void setLatitu(JSONObject latitu) {
		this.latitu = latitu;
	}

	public void setLatitudeList(JSONArray latitudeList) {
		this.latitudeList = latitudeList;
	}

	private JSONArray latitudeList;
	private String latitudeName;
	private Integer latitudeId;
	public Integer getLatitudeId() {
		return latitudeId;
	}
	public void setLatitudeId(Integer latitudeId) {
		this.latitudeId = latitudeId;
	}
	public void setLatitudeName(String latitudeName) {
		this.latitudeName = latitudeName;
	}

	private String cause;
	private String spcx;
	private String sectionname;

	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getSpcx() {
		return spcx;
	}
	public void setSpcx(String spcx) {
		this.spcx = spcx;
	}

	public String getSectionname() {
		return sectionname;
	}
	public void setSectionname(String sectionname) {
		this.sectionname = sectionname;
	}

	private String batchno;
	public String getBatchno() {
		return batchno;
	}
	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}
	
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

	private Integer  requestType;
	public Integer getRequestType() {
		return requestType;
	}
	public void setRequestType(Integer requestType) {
		this.requestType = requestType;
	}
	/*
	 * 新增纬度 民事
	 */
	public void save() {
		User user = isLogined();
		if (user == null) {
			if (userid != null) {
				User getusers = new User();
				getusers.setUsername(username);
				getusers.setUserid(Integer.valueOf(userid));
				getusers.setName(name);
				getusers.setRolecode(rolecode);
				Map msg = latitudeService.save(latitude, getusers,0);
				outJsonByMsg(msg, "成功");
				return;
			}
			outJsonByMsg("未登录");
			return;
		}
		Map msg = latitudeService.save(latitude, user,0);
		outJsonByMsg(msg, "成功");
	}

	/*
	 * 新增纬度 民事 刑事
	 */
	public void savePenal() {
		User user = isLogined();
		if (user == null) {
			if (userid != null) {
				User getusers = new User();
				getusers.setUsername(username);
				getusers.setUserid(Integer.valueOf(userid));
				getusers.setName(name);
				getusers.setRolecode(rolecode);
				Map msg = latitudeService.save(latitude, getusers,1);
				outJsonByMsg(msg, "成功");
				return;
			}
			outJsonByMsg("未登录");
			return;
		}
		Map msg = latitudeService.save(latitude, user,1);
		BatchConstant.LALIST_PENAL.clear();
		outJsonByMsg(msg, "成功");
	}
	
	private String userid;
	private String username;
	private String name;
	private String rolecode;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRolecode() {
		return rolecode;
	}

	public void setRolecode(String rolecode) {
		this.rolecode = rolecode;
	}
	

	/*
	 * 修改纬度名称和规则
	 */
	public void update() {
		User user = isLogined();
		if (user == null) {
			if (userid != null) {
				User getusers = new User();
				getusers.setUsername(username);
				getusers.setUserid(Integer.valueOf(userid));
				getusers.setName(name);
				getusers.setRolecode(rolecode);
				String msg = latitudeService.update(latitude, getusers);
				outJsonByMsg(msg);
				return;
			}
			outJsonByMsg("未登录");
			return;
		} else {
			String msg = latitudeService.update(latitude, user);
			outJsonByMsg(msg);
		}
	}

	/*
	 * 获取纬度树   民事
	 */
	public void getLatitudeList() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		//1 刑事 9民事
		List<Map> list = latitudeService.getLatitudeList(user,9);
		outJsonByMsg(list, "成功");
	}

	/*
	 * 获取纬度树   刑事
	 */
	public void getLatitudeListPenal() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		
		if (BatchConstant.LALIST_PENAL.isEmpty() ) {
			LOGGER.info("缓存为空  从mysql获取维度菜单列表");
			List<Map> list = latitudeService.getLatitudeList(user,1);
			outJsonByMsg(list, "成功");
			BatchConstant.LALIST_PENAL = list;
		}else {
			LOGGER.info("从缓存获取维度菜单列表");
			outJsonByMsg(BatchConstant.LALIST_PENAL, "成功");
		}
	}
	
	/*
	 * 根据ID获取纬度详细信息
	 */
	public void getLatitude() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		Integer userid = user.getUserid();
		Latitude latitudes = latitudeService.getLatitude(latitude);
		ViewhisBatch viewhisBatch = new ViewhisBatch();
		try {
			latitudes.setViewList(viewhisBatch.getViewhisListByRuleid(latitude.getLatitudeid()));
			Integer type = viewhisBatch.getTypeByLatitudeid(latitude.getLatitudeid());
			LOGGER.info("维度规则-新建预览需要的样本列表参数{不传默认民事} --> 民事:0  刑事:1 --> "+ requestType+" type "+type);
			requestType = 0;
			if (type == null || type == 1 || type == 9 || type == 99) {
				requestType = 1;
			}
			if (null == requestType || requestType != 0) {//民事
				latitudes.setSampleList(viewhisBatch.getSampleids(22,userid));
			}else {
				latitudes.setSampleList(viewhisBatch.getSampleids(21,userid));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		outJsonByMsg(latitudes, "成功");
	}

	/*
	 * 模糊查询纬度名称
	 */
	public void getLatitudeName() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		List<String> latitudes = latitudeService.getLatitudeName(latitude);
		outJsonByMsg(latitudes, "成功");

	}
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm____ss-");
	/*
	 * 规则预览   维度民事
	 */
	public void getDocList() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		if (null == sampleid || 0 == sampleid) {
			sampleid = 362;
		}
		String batch_no = sdf.format(new Date())+RandomUtils.nextInt(1000);
		ViewhisBatch viewhis = new ViewhisBatch();
		Viewhis vie = viewhis.saveHis(latitude, true, id,batch_no,sampleid);
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				SectionBatchPenal sectionBatchPenal = new SectionBatchPenal();
				List<StatsDoc> list = latitudeService.getDocList(latitude, user,1,sectionBatchPenal.getDocsectionanruleList(user.getUserid(),2,sampleid),batch_no);
				viewhis.saveViewhis(list,latitude,true,batch_no);
			}
		});
		threadPool.shutdown();
		outJsonByMsg(vie, "成功");
	}
	/*
	 * 规则预览   维度刑事
	 */
	public void getDocListPenal() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		if (null == sampleid || 0 == sampleid) {
			sampleid = 362;
		}
		logger.info("新增预览 ---> 样本id (默认362) ---> "+sampleid);
		String batch_no = sdf.format(new Date())+RandomUtils.nextInt(1000);
		ViewhisBatch viewhis = new ViewhisBatch();
		Viewhis vie = viewhis.saveHis(latitude, true, id,batch_no,sampleid);
		Integer id = vie.getId();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				SectionBatchPenal sectionBatchPenal = new SectionBatchPenal();
				List<StatsDoc> list = latitudeService.getDocList(latitude, user,1,sectionBatchPenal.getDocsectionanruleList(user.getUserid(),3,sampleid),batch_no);
				viewhis.saveViewhis(list,latitude,true,batch_no);
			}
		});
//		threadPool.shutdown();
		outJsonByMsg(vie, "成功");
	}
	/*
	 * 获取筛选页左侧列表
	 */
	public void getScreeList() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		List<Map> list = latitudeService.getScreeList(latitudeName, latitude.getLatitudeid());
		outJsonByMsg(list, "成功");
	}

	/*
	 * 跑批根据ID获取规则
	 */
	public Latitude getLatitudes() {
		latitude.setLatitudeid(latitude.getLatitudeid());
		Latitude latitudes = latitudeService.getLatitude(latitude);
		return latitudes;
	}

	/*
	 * 按照名称查询
	 */
	public void getLatitudeShow() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		String msg = "失败";
		if (latitude.getLatitudename() == null) {
			outJsonByMsg(msg);
		} else {
			List<Map> list = latitudeService.getLatitudeShow(latitude.getLatitudename(), user);
			outJsonByMsg(list, "成功");
		}
	}

	/*
	 * 按照规则查询
	 */
	public void getRuleShow() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		String msg = "失败";
		if (latitude.getLatitudeid() == null) {
			outJsonByMsg(msg);
		} else {
			List<Latitude> list = latitudeService.getRuleShow(latitude.getLatitudeid(), cause, spcx, sectionname);
		}
	}

	/*
	 * 修改名称
	 */
	public void updateName() {
		User user = isLogined();
		if (user == null) {
			if (userid != null) {
				User getusers = new User();
				getusers.setUsername(username);
				getusers.setUserid(Integer.valueOf(userid));
				getusers.setName(name);
				getusers.setRolecode(rolecode);
				String msg = latitudeService.updateName(latitude, getusers);
				outJsonByMsg(msg);
				return;
			}
			outJsonByMsg("未登录");
			return;
		}
		String msg = null;
		if (latitude.getLatitudeid() == null) {
			msg = "失败";
		} else {
			msg = latitudeService.updateName(latitude, user);
		}
		outJsonByMsg(msg);
	}

	/*
	 * 送审
	 */
	public void approve() {
		User user = isLogined();
		if (user == null) {
			outJsonByMsg("未登录");
			return;
		}
		String msg = latitudeService.approve(latitude, user);
		outJsonByMsg(msg);
	}
	
	
	/**
	 * 根据批次号查看维度预览历史
	 */
	public void getDocListHis() {
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		ViewhisBatch viewhis = new ViewhisBatch();
		List<StatsDoc> list = viewhis.getDocListByBatchno(batchno);
		outJsonByMsg(list, "成功");
	}
	
	
	/**
	 * 删除维度预览历史
	 */
	public void deleteHis() {
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		ViewhisBatch viewhis = new ViewhisBatch();
		viewhis.deleteHisByBatchno(batchno);
		outJsonByMsg("成功");
	}
	
	
}
