package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pluskynet.parsing.Parsing;
import com.pluskynet.util.BatchUtils;
import com.pluskynet.util.C3P0connsPollUTIL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
* @author HF
* @version 创建时间：2019年7月3日 上午9:47:16
* 类说明   所有文书信息提取   reason spcx doctype court ````
*/
public class ArticleInfo {

	static Map<String,Integer> idMap = new HashMap<String, Integer>();
	static Map<String,Integer> pidMap = new HashMap<String, Integer>();
	static Map<Integer,String> nMap = new HashMap<Integer,String>();
	
	public static void main(String[] args) {
		articleInfo();
	}
	public static void articleInfo() {
		Map<String,Long> map = new HashMap<String, Long>();
		Connection conn = null;
		Statement st = null;
		Statement st1 = null;
		ResultSet rs = null;
		int data = 2003;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			st = conn.createStatement();
			st1 = conn.createStatement();
			ResultSet executeQuery2 = st1.executeQuery("SELECT" + 
					"					pc.pc_id," + 
					"					pc.pc_order," + 
					"					latitudename AS pc_name," + 
					"					pg.pg_id," + 
					"					case when ifnull(pg.pg_show,'') != '' then pg.pg_show else pg.pg_name end as pg_name," + 
					"					po.po_id," + 
					"					po.po_name," + 
					"					po.po_pid," + 
					"					po.po_tier," + 
					"					po.po_order," + 
					"					po.po_type," + 
					"					po.po_link," + 
					"					po.po_relation," + 
					"					po.po_propValue," + 
					"					po.po_propKey," + 
					"					po.po_propRelate," + 
					"					po.po_prop," + 
					"					po.po_rangeType," + 
					"					pv.pv_id," + 
					"					pv.pv_vectorId " + 
					"				FROM" + 
					"					t_para_cri pc" + 
					"					INNER JOIN latitude l ON latitudeid = pc.pc_causeId" + 
					"					INNER JOIN t_para_cri_grp pcg ON pcg.pc_id = pc.pc_id" + 
					"					LEFT JOIN t_para_grp pg ON pg.pg_id = pcg.pg_id" + 
					"					LEFT JOIN t_para_one po ON po.pg_id = pg.pg_id" + 
					"					LEFT JOIN t_para_vector pv ON pv.po_id = po.po_id " + 
					"				ORDER BY" + 
					"					pc.pc_order," + 
					"					pc.pc_id," + 
					"					pg.pg_id," + 
					"					po.po_tier," + 
					"					po.po_order," + 
					"					po.po_id," + 
					"					pv.pv_id");
			while (executeQuery2.next()) {
				idMap.put(executeQuery2.getString("po_name"), executeQuery2.getInt("po_id"));
				pidMap.put(executeQuery2.getString("po_name"), executeQuery2.getInt("po_pid"));
				nMap.put(executeQuery2.getInt("po_id"), executeQuery2.getString("po_name"));
			}
			ResultSet executeQuery = st.executeQuery("select id,dict_name from article_dict");
			while (executeQuery.next()) {
				map.put(executeQuery.getString("dict_name"), executeQuery.getLong("id"));
			}
			String sql ="";
			while(data <= 2018) {
				rs = st.executeQuery("select * from article"+data+"_decode where state = 3 limit 1");
				if (BatchUtils.isBlank(rs)) {
					System.out.println(data+"");
					data++;
					continue;
				}
				String insertSql = "insert into z_ck_test(doc_id,data,reason,ccourtid,action,appellor,court,casetype,yeardate,courtprovinces,date"
						+ ",courtcities,coid,name,title,step,level,year";
				String values = "";
				String idIn = "(";
				while(rs.next()) {
					sql = "";
					int docstats = 0;
					idIn += rs.getLong("id")+",";
					JSONObject jsonObject = new JSONObject().fromObject(rs.getString("decode_data"));
					JSONObject jsonObject2 = new JSONObject().fromObject(jsonObject.getString("dirData"));
					JSONObject jsonObject3 = new JSONObject().fromObject(jsonObject.getString("htmlData"));
					JSONObject jsonObject4 = new JSONObject().fromObject(jsonObject.getString("caseinfo"));
					String reason = "";
					String spcx = "";
					String doctype = "";
					String casetype = "";
					String court = "";
					String ccourtid = "null";
					String caseno = "";
					String courtcities = "";
					String courtprovinces = "";
					String casename = "";
					String trialDate = "";
					String appellor = "";
					
//					if (jsonObject3.containsKey("Html")) {
//						if (jsonObject3.getString("Html").length() < 10) {
//							docstats = 1;
//						}
//					}else {
//						docstats = 1;
//					}
					String title = "";
					if (jsonObject3.containsKey("Title")) {
						title = jsonObject3.getString("Title");
						doctype = BatchUtils.getDoctypeByTitle(jsonObject3.getString("Title"));
					}
					if (jsonObject2.containsKey("RelateInfo")) {
						JSONArray jsonArray = new JSONArray().fromObject(jsonObject2.getString("RelateInfo"));
						for (int j = 0; j < jsonArray.size(); j++) {
							JSONObject js = new JSONObject().fromObject(jsonArray.get(j));
							if (js.getString("key").equals("caseType")) {
								casetype = js.getString("value");
							}
							if (js.getString("key").equals("reason")) {
								reason = js.getString("value");
							}
							if (js.getString("key").equals("court")) {
								court = js.getString("value");
							}
							if (js.getString("key").equals("trialRound")) {
								spcx = js.getString("value");
							}
							if (js.getString("key").equals("trialDate")) {
								trialDate = js.getString("value");
							}
							if (js.getString("key").equals("appellor")) {
								appellor = js.getString("value");
							}
						}
					}
					if (appellor.length() > 240) {
						appellor = "data too lang";
					}
					for (int j = 0; j < jsonObject4.size(); j++) {
						ccourtid = jsonObject4.getString("法院ID").toString();
						caseno = jsonObject4.getString("案号").toString();
						courtcities = jsonObject4.getString("法院地市").toString();
						courtprovinces = jsonObject4.getString("法院省份").toString();
						casename = jsonObject4.getString("案件名称").toString();
					}
					if (null == ccourtid || "".equals(ccourtid)) {
						ccourtid = "null";
					}
					int cType = 1;
					if ("民事案件".equals(casetype)) {
						cType = 0;
					}
					
					String[] split = reason.split(",");
					reason = split[0];
//					insertSql += "('"+rs.getString("doc_id")+"',"+map.get(spcx)+","+map.get(doctype)+","+map.get(court)+","+cType+
//							","+map.get(reason)+",'"+trialDate+"',"+ccourtid+",'"+caseno.replace("'", "")+"',"+map.get(courtcities)+
//							","+map.get(courtprovinces)+","+data+","+docstats+",'"+appellor+"'),";
					values += "('"+rs.getString("doc_id")+"','"+Parsing.getTextFromHtml(jsonObject3.getString("Html")).replace("'", "").replace("\\", "")
							+"','"+reason+"',"+ccourtid+",'"+spcx+"','"+appellor+"','"+court+"','"+casetype+"',"+trialDate+",'"+courtprovinces+"',"
							+trialDate.substring(0,4)+",'"+courtcities+"','"+caseno+"','"+title+"','"+title+"','"+spcx+"','"+court+"',"+trialDate.substring(0,4);
					List<String> list = new ArrayList<String>();
			
					list.add(reason);
					list.add(spcx);
					list.add(court);
					list.add(casetype);
				
					sql = getSqlByList(insertSql, values,list);
				}
//				idIn = idIn.substring(0,idIn.length()-1)+")";
//				insertSql = insertSql.substring(0,insertSql.length()-1);
				st.addBatch(sql);
				st.executeBatch();
//				st1.executeUpdate("update article"+data+"_decode set state = 4 where id in"+idIn);
				conn.commit();
				st.clearBatch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(conn, st1, rs);}
	}
	
	
	public static String getSqlByList(String sqlStart,String values,List<String> list) {
		for (String string : list) {
			appendSqlStart(string, sqlStart,values);
		}
		return sqlStart+") values "+values+")";
	}
	
	public static void appendSqlStart(String str,String sqlStart,String values) {
		if (idMap.containsKey(str)) {
			Integer integer = idMap.get(str);
			sqlStart += ",cond_"+integer;
			values += ",1";
		}
		if (pidMap.containsKey(str)) {
			Integer integer = pidMap.get(str);
			if (0 == integer) {
				return;
			}else {
				appendSqlStart(nMap.get(integer), sqlStart,values);
			}
		}
	}
}
