package com.pluskynet.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jsoup.helper.StringUtil;

import com.pluskynet.batch.SampleBatch;
import com.pluskynet.domain.Sample;
import com.pluskynet.domain.User;
import com.pluskynet.service.SampleService;
import com.pluskynet.util.BaseAction;
import com.pluskynet.util.ThreadPoolSingleton;


public class SampleAction extends BaseAction{
	private static Logger LOGGER = Logger.getLogger(SampleAction.class);
	private Sample sample;
	private List<Sample> sampleList;
	SampleBatch sampleBatch = new SampleBatch();
	@Override
	public Object getModel() {
		sample = new Sample();
		return sample;
	}
	private SampleService sampleService;
	public void setSampleService(SampleService sampleService) {
		this.sampleService = sampleService;
	}
	
	private Integer id;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	private ThreadPoolSingleton instance = ThreadPoolSingleton.getinstance();
/**
 * 民事段落样本
 */
	public void random(){
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		LOGGER.info(" 民事段落样本saveorupdate ---  用户名---"+user.getUsername()+"---样本id---"+sample.getId());
		if (sample.getId() != null && sampleBatch.getStateByid(sample.getId())) {
			outJsonByMsg("正在处理该样本,请稍等...");
			return;
		}
		sampleService.randomNew(sample, user,11);//11民事段落12刑事段落 21民事维度 22刑事维度
		String rule = sample.getRule();
		LOGGER.info(sample.getId());
		if (!StringUtil.isBlank(rule) || !"[]".equals(rule)) {
			instance.executeThread(new SampleBatch(user, sample, 0));
		}
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("id", sample.getId());
		outJsonByMsg(map,"成功");
	}
	/**
	 * 刑事段落样本
	 */
	public void randomPenal(){
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		LOGGER.info(" 刑事段落样本saveorupdate ---  用户名---"+user.getUsername()+"---样本id---"+sample.getId());
		if (sample.getId() != null && sampleBatch.getStateByid(sample.getId())) {
			outJsonByMsg("正在处理该样本,请稍等...");
			return;
		}
		sampleService.randomNew(sample, user,12);//11民事段落12刑事段落 21民事维度 22刑事维度
		String rule = sample.getRule();
		if (!StringUtil.isBlank(rule) || !"[]".equals(rule)) {
			instance.executeThread(new SampleBatch(user, sample, 1));
		}
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("id", sample.getId());
		outJsonByMsg(map,"成功");
	}
	/**
	 * 保存 维度样本 民事
	 */
	public void randomLatitude(){
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		LOGGER.info(" 民事维度样本saveorupdate ---  用户名---"+user.getUsername()+"---样本id---"+sample.getId());
		if (sample.getId() != null && sampleBatch.getStateByid(sample.getId())) {
			outJsonByMsg("正在处理该样本,请稍等...");
			return;
		}
		sampleService.randomNew(sample, user,21);//11民事段落12刑事段落 21民事维度 22刑事维度
		String rule = sample.getRule();
		if (!StringUtil.isBlank(rule) || !"[]".equals(rule)) {
			instance.executeThread(new SampleBatch(user, sample, 2));
		}
		
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("id", sample.getId());
		outJsonByMsg(map,"成功");
	}
	/**
	 * 保存 维度样本 刑事
	 */
	public void randomLatitudePenal(){
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		try {
			if (sample.getId() != null && sampleBatch.getStateByid(sample.getId())) {
				outJsonByMsg("正在处理该样本,请稍等...");
				return;
			}
			LOGGER.info(" 刑事维度样本saveorupdate ---  用户名---"+user.getUsername()+"---样本id---"+sample.getId());
			sampleService.randomNew(sample, user,22);//11民事段落12刑事段落 21民事维度 22刑事维度
			String rule = sample.getRule();
			if (!StringUtil.isBlank(rule) || !"[]".equals(rule)) {
				instance.executeThread(new SampleBatch(user, sample, 3));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("id", sample.getId());
		outJsonByMsg(map,"成功");
	}
	/**
	 * 段落样本列表 民事
	 */
	public void select(){
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		sampleList = sampleService.select(user,11);
		outJsonByMsg(sampleList, "成功");
	}
	/**
	 * 段落样本列表 刑事
	 */
	public void selectPenal(){
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		sampleList = sampleService.select(user,12);
		outJsonByMsg(sampleList, "成功");
	}
	/**
	 * 维度样本列表  民事
	 */
	public void selectLatitude(){
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		sampleList = sampleService.select(user,21);
		outJsonByMsg(sampleList, "成功");
	}
	/**
	 * 维度样本列表  刑事
	 */
	public void selectLatitudePenal(){
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		sampleList = sampleService.select(user,22);
		outJsonByMsg(sampleList, "成功");
	}
	
	/**
	 * 删除
	 */
	public void deleteSample(){
		User user = isLogined();
		if (user==null) {
			outJsonByMsg("未登录");
			return;
		}
		LOGGER.info(" 样本删除 ---  用户名---"+user.getUsername()+"---样本id---"+sample.getId());
		sampleService.deleteSample(sample);
		outJsonByMsg("成功");
	}
}
