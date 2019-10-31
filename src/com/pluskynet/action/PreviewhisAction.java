package com.pluskynet.action;

import java.util.List;

import com.pluskynet.domain.Latitudestatistical;
import com.pluskynet.domain.Previewhis;
import com.pluskynet.service.PreviewhisService;
import com.pluskynet.util.BaseAction;

public class PreviewhisAction extends BaseAction{
	private Previewhis previewhis;

	@Override
	public Object getModel() {
		previewhis = new Previewhis();
		return previewhis;
	}
	private String sample;
	private String createtime;
	private Integer sum;
	private Integer accord;
	private Integer noaccord;
	private String createuser;
	private String createname;
	
	public String getSample() {
		return sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public Integer getSum() {
		return sum;
	}

	public void setSum(Integer sum) {
		this.sum = sum;
	}

	public Integer getAccord() {
		return accord;
	}

	public void setAccord(Integer accord) {
		this.accord = accord;
	}

	public Integer getNoaccord() {
		return noaccord;
	}

	public void setNoaccord(Integer noaccord) {
		this.noaccord = noaccord;
	}

	public String getCreateuser() {
		return createuser;
	}

	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}

	public String getCreatename() {
		return createname;
	}

	public void setCreatename(String createname) {
		this.createname = createname;
	}
	private PreviewhisService previewhisService;
	public void setPreviewhisService(PreviewhisService previewhisService) {
		this.previewhisService = previewhisService;
	}
	private String starttime;
	private String endtime;
	
	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	
	private Previewhis getpreviewhis = new Previewhis(sample, createtime, sum, accord, noaccord, createuser, createname);

	/*
	 * 查询预览历史
	 */
	public void select(){
		List<Previewhis> list = previewhisService.select(starttime,endtime);
		outJsonByMsg(list, "成功");
	}
	/*
	 * 保存预览历史
	 */
	public void save(){
		if (previewhis==null) {
			previewhisService.save(getpreviewhis);
			return;
		}
		previewhisService.save(previewhis);
	}
	/*
	 * 结果数据展示
	 */
	public void latitudestatistical(){
		List<Latitudestatistical> list = previewhisService.latitudestatistical();
		outJsonByMsg(list, "成功");
	}
}
