package com.pluskynet.data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pluskynet.domain.Preview;
import com.pluskynet.util.C3P0connsPollUTIL;
/**
* @author HF
* @version 创建时间：2019年4月18日 下午3:40:59
* 类说明
* 排除“当事人”含有以下关键词的文书
*/
public class KeywordAppellor {
	public static final List<String> KEY_WORD_APPELLOR = new ArrayList<String>(Arrays.asList("集团","控股","中国","国家","国营","国有","证券","投资","基金","保险资产管理","信托","期货","银行","保险","融资租赁","汽车金融"));
	public static String[] DATA_YEARS = {"2003","2004","2005","2006","2007","2008","2010","2011","2012","2013","2014","2015","2016","2017","2018",}; 
	public static String[] DATA_SPCXS = {"一审","二审"}; 
	public static Map<String,List<Preview>> previewMap = new HashMap<String,List<Preview>>();
	public static void initPreviewList(String spcx) {
		Connection wenshuConn = null;
		Statement wenshuStmt = null;
		ResultSet wenshuRs = null;
		List<Preview> previewList = new ArrayList<Preview>();
		try {
			wenshuConn = C3P0connsPollUTIL.getConnection();
			wenshuStmt = wenshuConn.createStatement();
			wenshuRs = wenshuStmt.executeQuery("select * from latitudeaudit where stats = '0' and rule like '%trialRound\":\""+spcx+"%' and rule is not null and rule != '[]' and rule != '' and latitudetype= 0 and casetype = 1  and id in (94,105)");
//			wenshuRs = wenshuStmt.executeQuery("select * from latitudeaudit where stats = '0' and rule like '%trialRound\":\""+spcx+"%' and rule is not null and rule != '[]' and rule != '' and latitudetype= 0 and casetype = 1 ");
			while (wenshuRs.next()) {
				Preview preview = new Preview();
				preview.setRule(wenshuRs.getString("rule"));
				preview.setCheckId(wenshuRs.getInt("latitudeid")+"");
				preview.setDocName(wenshuRs.getString("latitudename"));
				previewList.add(preview);
			}
//			wenshuRs = wenshuStmt.executeQuery("SELECT * FROM docrule WHERE type = 1 and fid != 0 and rule like '%trialRound\":\""+spcx+"%'");
//			while (wenshuRs.next()) {
//				Preview preview = new Preview();
//				preview.setRule(wenshuRs.getString("rule"));
//				preview.setCheckId(wenshuRs.getInt("ruleid")+"");
//				preview.setDocName(wenshuRs.getString("sectionName"));
//				previewList.add(preview);
//			}
			previewMap.put(spcx, previewList);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			C3P0connsPollUTIL.close(wenshuConn, wenshuStmt, wenshuRs);
		}
	}
}
