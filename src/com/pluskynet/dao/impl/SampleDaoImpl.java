package com.pluskynet.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jsoup.helper.StringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pluskynet.batch.BatchConstant;
import com.pluskynet.batch.SampleBatch;
import com.pluskynet.dao.SampleDao;
import com.pluskynet.dao.SynonymDao;
import com.pluskynet.domain.Article01;
import com.pluskynet.domain.Articleyl;
import com.pluskynet.domain.Docsectionandrule;
import com.pluskynet.domain.Docsectionandrule01;
import com.pluskynet.domain.Rule;
import com.pluskynet.domain.Sample;
import com.pluskynet.domain.User;
import com.pluskynet.service.SampleService;
import com.pluskynet.util.JSONUtil;

public class SampleDaoImpl extends HibernateDaoSupport implements SampleDao {
	private static Logger LOGGER = Logger.getLogger(SampleDaoImpl.class);
	private SessionFactory sessionFactory;

	@Override
	@Transactional
	public List<Article01> getListArticle(String table, String year, int count,String trialRound,String doctype,User user) {
//		String hql = "SELECT  * FROM "+table+" AS t1 JOIN (SELECT ROUND(RAND() * ((SELECT MAX(id) FROM "+table+" where spcx='"+trialRound+"' and doctype='"+doctype+"' and date = '"+year+"') - (SELECT MIN(id) FROM "+table+" where spcx='"+trialRound+"' and doctype='"+doctype+"' and date = '"+year+"')) + (SELECT MIN(id) FROM "+table+" where spcx='"+trialRound+"' and doctype='"+doctype+"' and date = '"+year+"')) AS id) AS t2 WHERE t1.id >= t2.id and t1.spcx='"+trialRound+"' and t1.doctype='"+doctype+"' and t1.date = '"+year+"' ORDER BY  t1.id LIMIT "+count+";";
		String hql = "SELECT * FROM "+table+" WHERE id >= ((SELECT MAX(id) FROM "+table+" t1 WHERE  t1.spcx='"+trialRound+"' and t1.doctype='"+doctype+"' and t1.date = '"+year+"')-(SELECT MIN(id) FROM "+table+" t1 WHERE  t1.spcx='"+trialRound+"' and t1.doctype='"+doctype+"' and t1.date = '"+year+"')) * RAND() + (SELECT MIN(id) FROM "+table+" t1 WHERE  t1.spcx='"+trialRound+"' and t1.doctype='"+doctype+"' and t1.date = '"+year+"') and  spcx='"+trialRound+"' and doctype='"+doctype+"' and date = '"+year+"' LIMIT "+count+" ;";
//		String hql = "{CALL sql_queryData('"+table+"',"+year+","+count+",'"+trialRound+"','"+doctype+"','"+user.getName()+"',"+user.getUserid()+")}";
		Session session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();
//		session.createSQLQuery(hql).executeUpdate();
		List<Article01> list = session.createSQLQuery(hql).addEntity(Article01.class).list();
		return list;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pluskynet.dao.SampleDao#save(java.util.List,
	 * com.pluskynet.domain.User) 保存段落样本
	 */
	@Transactional
	public void save(List<Article01> list, User user) {
		Session session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();
		Connection conn = session.connection();
		Transaction tx = session.beginTransaction();
		for (int i = 0; i < list.size(); i++) {
			String sql = "INSERT INTO `articleyl` (`doc_id`, `title`, `decode_data`, `belonguser`, `belongid`) VALUES ('"
					+ list.get(i).getDocId() + "','" + list.get(i).getTitle() + "',?,'" + user.getUsername() + "'," + user.getUserid() + ")";
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, list.get(i).getDecodeData());
				stmt.addBatch();
				stmt.executeBatch();
				if (i % 100 == 0 || i == list.size() - 1) {
					conn.setAutoCommit(false);
					conn.commit();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*for (int i = 0; i < list.size(); i++) {
			Articleyl articleyl = new Articleyl();
			articleyl.setDecodeData(list.get(i).getDecodeData());
			articleyl.setDocId(list.get(i).getDocId());
			articleyl.setTitle(list.get(i).getTitle());
			articleyl.setBelongid(user.getUserid());
			articleyl.setBelonguser(user.getUsername());
			this.getHibernateTemplate().save(articleyl);
		}*/
	}

	@Override
	@Transactional
	public void saverule(Sample sample, User user,int type) {
		LOGGER.info(user.getUsername()+" 保存样本随机抽取条件");
		String rule = sample.getRule();
		if (StringUtil.isBlank(rule) || "[]".equals(rule)) {
			LOGGER.info("初始新增样本 规则为空 不作处理... sampleid --> "+sample.getId());
			sample.setState("完成,共0条");
		}else {
			if (BatchConstant.LA_ALL.isEmpty() || BatchConstant.LA_ALL.size() < 1) {
				BatchConstant.initLaallMap();
			}
			String reserved = sample.getReserved();
			JSONArray jsonarr = (JSONArray) JSONArray.parse(reserved);
			for (int i = 0; i < jsonarr.size(); i++) {
				JSONObject jsonobj = (JSONObject) jsonarr.get(i);
				String laids = JSONObject.toJSONString(jsonobj.get("latitudename"));
				laids = laids.substring(1,laids.length()-1);
				String[] split = laids.split(",");
				String latitudename = "";
				for (String string : split) {
					latitudename += BatchConstant.LA_ALL.get( Integer.valueOf(string))+",";
				}
				latitudename = latitudename.substring(0,latitudename.length()-1);
				jsonobj.put("latitudename", latitudename);
				
				String laides =JSONObject.toJSONString(jsonobj.get("latitudename_except"));
				laides = laides.substring(1,laides.length()-1);
				String[] split2 = laides.split(",");
				String latitudename_ = "";
				for (String string : split2) {
					latitudename_ += BatchConstant.LA_ALL.get(Integer.valueOf(string))+",";
				}
				latitudename_ = latitudename_.substring(0,latitudename_.length()-1);
				jsonobj.put("latitudename_except", latitudename_);
			}
			sample.setReserved(jsonarr.toJSONString());
			sample.setState("正在处理");
		}
		
//		String hql = "delete From sample where belongid = " + user.getUserid() + " and type = "+type;
		Session session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();
//		session.createSQLQuery(hql).executeUpdate();
		sample.setBelongid(user.getUserid());
		sample.setType(type);
		sample.setBelonguser(user.getUsername());
		this.getHibernateTemplate().saveOrUpdate(sample);;
	}
	
	@Override
	public void deleteSample(Sample sample) {
		this.getHibernateTemplate().delete(sample);
	}
	
	@Override
	public void updateSample(Sample sample) {
		this.getHibernateTemplate().update(sample);
	}
	
	@Override
	public List<Sample> findById(Sample sample) {
		List<Sample> findByExample = this.getHibernateTemplate().findByExample(sample);
		 return findByExample;
	}
	@Override
	public List<Sample> select(User user,int type) {
		String hql = "from Sample where belongid = " + user.getUserid() + " and type = "+type;
		List<Sample> list = this.getHibernateTemplate().find(hql);
		if (list.size() > 0) {
			return list;
		}
		return null;

	}

	@Override
	@Transactional
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pluskynet.dao.SampleDao#delete(com.pluskynet.domain.User) 删除段落样本
	 */
	public void delete(User user) {
		String hql = "delete From articleyl where belongid = " + user.getUserid() + "";
		Session session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();
		session.createSQLQuery(hql).executeUpdate();
	}

	@Override
	@Transactional
	public void deleteDoc(User user) {
		String hql = "delete From docsectionandrule where belongid = " + user.getUserid() + "";
		Session session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();
		session.createSQLQuery(hql).executeUpdate();
	}

	@Override
	@Transactional	
	public void saveDoc(List<Docsectionandrule01> list, User user) {
		for (int i = 0; i < list.size(); i++) {
			Docsectionandrule docsectionandrule = new Docsectionandrule();
			docsectionandrule.setRuleid(list.get(i).getRuleid());
			docsectionandrule.setSectiontext(list.get(i).getSectiontext());
			docsectionandrule.setDocumentsid(list.get(i).getDocumentsid());
			docsectionandrule.setSectionname(list.get(i).getSectionname());
			docsectionandrule.setTitle(list.get(i).getTitle());
			docsectionandrule.setBelongid(user.getUserid());
			docsectionandrule.setBelonguser(user.getUsername());
			this.getHibernateTemplate().save(docsectionandrule);
		}

	}

}
