package com.pluskynet.quartz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.pluskynet.batch.BatchConstant;
import com.pluskynet.batch.CivilSectionQuartz;
import com.pluskynet.domain.Preview;
import com.pluskynet.util.JDBCPoolUtil;

/**
* @author HF
* @version 创建时间：2019年11月1日 上午9:49:18
* 类说明		定时分段    民事判决书
*/
public class SectionQuartz {

	private Logger LOGGER = Logger.getLogger(SectionQuartz.class);
	public void sectionQuartzTaskStart() {
		LOGGER.info("start");
		List<Map<String, Object>> list = JDBCPoolUtil.selectBySql("SELECT a.batch_no as batch_no,b.rule as rule,b.sectionName as sectionName,b.ruleid as ruleid  FROM t_quartz_data a left join docrule b on a.ruleid = b.ruleid WHERE " + 
				" a.ruletype = 10 and a.state is null and b.rule is not null and b.rule not in ('','[]')");
		List<Preview> previewList = new ArrayList<Preview>();
		Optional.ofNullable(list).orElse(getPreviewList()).parallelStream().forEach(o -> {
			Preview preview = new Preview();
			preview.setRule(o.get("rule").toString());
			preview.setCheckId(o.get("ruleid").toString());
			preview.setDocName(o.get("sectionName").toString());
			previewList.add(preview);
		});
		if (previewList.isEmpty()) {
			LOGGER.info("end --- previewList is empty");
			return;
		}
		while (LocalDateTime.now().getHour() >= 18 || LocalDateTime.now().getHour() <= 8) {
			for (String year : BatchConstant.DATA) {
				for (String spcx : BatchConstant.SPCX) {
					CivilSectionQuartz.sectionBatchExecuteThread(year, spcx, previewList);
				}
			}
		}
		
		Integer count = (Integer) JDBCPoolUtil.selectBySql("select count(*) as count from article_fiter_pjs where batch_no is null").get(0).get("count");
		if (count == 0) {
			JDBCPoolUtil.executeSql("update t_quartz_data set state = 0 where ruletype = 10 and state is null");
		}
	}
	
	
	private List<Map<String, Object>> getPreviewList(){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String batchno = UUID.randomUUID().toString();
		list = JDBCPoolUtil.selectBySql("select ruleid,rule,sectionName from docrule where rulestate = '完成' and rule is not null and rule not in ('','[]') " + 
				" and ruleid not in (SELECT ruleid FROM t_quartz_data)");
		if (null != list && list .size() > 0) {//有可分段规则
			JDBCPoolUtil.executeSql("update article_fiter_pjs set batch_no = null");//初始化所有batchno
			String insertSql = "insert into t_quartz_data(ruleid,ruletype,batch_no) values (";
			for (Map<String, Object> map : list) {
				insertSql += map.get("ruleid")+",10,'"+batchno+"',";
			}
			insertSql = insertSql.substring(0,insertSql.length()-1)+")";
			JDBCPoolUtil.executeSql(insertSql);//生成定时分段任务
		}
		return list;
	}
}
