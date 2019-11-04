package com.pluskynet.batch.thread;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.helper.StringUtil;

import com.pluskynet.batch.BatchConstant;
import com.pluskynet.domain.Docrule;
import com.pluskynet.domain.Latitude;
import com.pluskynet.domain.Rule;
import com.pluskynet.util.JDBCPoolUtil;

/**
* @author HF
* @version 创建时间：2019年11月1日 上午10:28:31
* 类说明
*/
public class RuleStateThread implements Runnable{

	private Logger LOGGER = Logger.getLogger(RuleStateThread.class);
	private Docrule docrule;
	private Latitude latitude;
	private String rulestate;;
	private Integer ruleid;
	private List<Map<String, Object>> list;
	public RuleStateThread() {
		super();
	}
	public RuleStateThread(Latitude latitude) {
		super();
		this.latitude = latitude;
		this.rulestate = latitude.getRulestate();
		this.ruleid = latitude.getLatitudeid();
		LOGGER.info(latitude.toString());
	}
	public RuleStateThread(Docrule docrule) {
		super();
		this.docrule = docrule;
		this.rulestate = docrule.getRulestate();
		this.ruleid = docrule.getRuleid();
		LOGGER.info(docrule.toString());
	}

	
	@Override
	public void run() {
		if (StringUtil.isBlank(rulestate)) {
			LOGGER.info("rulestate = { "+rulestate+" }");
			return;
		}
		String sql = "select id,ruleid,ruletype,state,year,lastid,type from t_quartz_data where ruleid = "+ruleid;
		list = JDBCPoolUtil.selectBySql(sql);
		if ((null == list || list.size() < 1) && "未完成".equals(rulestate)) {
			LOGGER.info("未完成 list == null");
			return;
		}
		JDBCPoolUtil.executeSql(updateSql());
	}
	
	private String updateSql() {
		StringBuffer update = new StringBuffer("");
		if ("未完成".equals(rulestate)) {
			update.append("update t_quartz_data set type = 0 where ruleid = ").append(ruleid);
		}else {
			if (null == list || list.size() < 1) {//首次标记完成 直接insert
				Integer ruletype;/** 10:民事段落规则,11:民事维度规则,20:刑事段落规则,21:刑事维度规则 **/
				if (docrule != null) {
					ruletype = (Integer) JDBCPoolUtil.selectBySql("select type from docrule where ruleid = "+ruleid).get(0).get("type");
					ruletype = ruletype == 0 ? 10 : 11;
					String[] years = BatchConstant.DATA;
					update.append("insert into t_quartz_data(ruleid,ruletype,year) values ");
					for (String year : years) {
						update.append("(").append(ruleid).append(",").append(ruletype).append(",").append(year).append("),");
					}
					update = new StringBuffer(update.toString().substring(0, update.length()-1));
				}

				if (latitude != null) {
					ruletype = (Integer) JDBCPoolUtil.selectBySql("select type from latitude where latitudeid = "+ruleid).get(0).get("type");
					ruletype = ruletype == 0 ? 20 : 21;
					update.append("insert into t_quartz_data(ruleid,ruletype) values ("+ruleid+","+ruletype+")");
				}
			}else {//非首次标记完成 需要update
				update.append("update t_quartz_data set type = null where ruleid = "+ruleid);
			}
		}
		return update.toString();
	}
}
