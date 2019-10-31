package com.pluskynet.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.types.CommandlineJava.SysProperties;
import org.springframework.transaction.annotation.Transactional;

import com.pluskynet.dao.DocRuleDao;
import com.pluskynet.dao.DocSectionAndRuleDao;
import com.pluskynet.dao.LatitudeauditDao;
import com.pluskynet.dao.LatitudenumDao;
import com.pluskynet.domain.Docrule;
import com.pluskynet.domain.Docsectionandrule;
import com.pluskynet.domain.Latitude;
import com.pluskynet.domain.Latitudeaudit;
import com.pluskynet.domain.Latitudenum;
import com.pluskynet.domain.User;
import com.pluskynet.domain.Viewhis;
import com.pluskynet.otherdomain.Otherdocrule;
import com.pluskynet.otherdomain.TreeDocrule;
import com.pluskynet.otherdomain.Treelatitude;
import com.pluskynet.service.DocRuleService;
import com.pluskynet.util.HttpRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings("all")
public class DocRuleServiceImpl implements DocRuleService {
	private LatitudeauditDao latitudeauditDao;

	public void setLatitudeauditDao(LatitudeauditDao latitudeauditDao) {
		this.latitudeauditDao = latitudeauditDao;
	}

	private DocRuleDao docRuleDao;

	public void setDocRuleDao(DocRuleDao docRuleDao) {
		this.docRuleDao = docRuleDao;
	}

	public DocSectionAndRuleDao docSectionAndRuleDao;

	public void setDocSectionAndRuleDao(DocSectionAndRuleDao docSectionAndRuleDao) {
		this.docSectionAndRuleDao = docSectionAndRuleDao;
	}

	private LatitudenumDao latitudenumDao;

	public void setLatitudenumDao(LatitudenumDao latitudenumDao) {
		this.latitudenumDao = latitudenumDao;
	}

	@Override
	public Map save(Docrule docrule,Integer type) {
		Map map = docRuleDao.save(docrule,type);
		return map;
	}

	@Override
	public String update(Docrule docrule) {
		String msg = docRuleDao.update(docrule);
		if (msg.equals("成功")) {
			Map<?, ?> map = docRuleDao.getDcoSection(docrule);
			String sectionname = map.get("sectionname").toString();
			docrule.setSectionname(sectionname);
			Latitudeaudit latitudeaudit = new Latitudeaudit();
			latitudeaudit.setRule(docrule.getRule());
			latitudeaudit.setLatitudename(sectionname);
			latitudeaudit.setLatitudeid(docrule.getRuleid());
			latitudeaudit.setLatitudetype(0);
			latitudeaudit.setReserved(docrule.getReserved());
			latitudeauditDao.update(latitudeaudit);
		}
		return msg;
	}

//	@Override
//	public List<TreeDocrule> getDcoSectionList(Integer type) {
//		//查出文书段落规则表(docRule)所有数据 按fid排序
//		List<Docrule> friList = docRuleDao.getDcoSectionList(type);
//		//查出维度数量表Latitudenum中type为0的(段落维度)
//		List<Latitudenum> doclist = latitudenumDao.getnums(0);
//		//所有节点
//		List<TreeDocrule> lists = new ArrayList<TreeDocrule>();
//		for (int i = 0; i < friList.size(); i++) {
//			//单个节点
//			TreeDocrule treeDocrule = new TreeDocrule();
//			treeDocrule.setFid(friList.get(i).getFid());
//			//段落名称
//			treeDocrule.setSectionname(friList.get(i).getSectionname() + "," + 0);
//			for (int j = 0; j < doclist.size(); j++) {
//				//属于段落维度的节点 重新设置段落名称
//				if (doclist.get(j).getLatitudeid() == friList.get(i).getRuleid()) {
//					treeDocrule.setSectionname(friList.get(i).getSectionname() + "," + doclist.get(j).getNums());
//					break;
//				}
//			}
//			//设置规则id添加到所有节点集合
//			treeDocrule.setRuleid(friList.get(i).getRuleid());
//			lists.add(treeDocrule);
//		}
//		//段落名称列表集合
//		List<TreeDocrule> list = new ArrayList<TreeDocrule>();
//		for (TreeDocrule tree : lists) {
//			//fid为0则为叶子节点
//			if (tree.getFid() == 0) {
//				list.add(tree);
//			}
//			for (TreeDocrule t : lists) {
//				//ruleId为此叶子节点id的
//				if (t.getFid() == tree.getRuleid()) {
//					//子节点为空
//					if (tree.getChildren() == null) {
//						//创建子节点集合 并添加进去
//						List<TreeDocrule> myChildrens = new ArrayList<TreeDocrule>();
//						myChildrens.add(t);
//						tree.setChildren(myChildrens);
//					} else {
//						//添加子节点
//						tree.getChildren().add(t);
//					}
//				}
//			}
//		}
//		return list;
//	}
	@Override
	public List<TreeDocrule> getDcoSectionList(Integer type) {
		List<Docrule> friList = docRuleDao.getDcoSectionList(type);
		List<Latitudenum> doclist = latitudenumDao.getnums(0);
		List<TreeDocrule> lists = new ArrayList<TreeDocrule>();
		for (int i = 0; i < friList.size(); i++) {
			TreeDocrule treeDocrule = new TreeDocrule();
			treeDocrule.setFid(friList.get(i).getFid());
			treeDocrule.setSectionname(friList.get(i).getSectionname() + "," + 0);
			treeDocrule.setRulestate(friList.get(i).getRulestate());
//			treeDocrule.setVieSet(friList.get(i).getVieSet());
			for (int j = 0; j < doclist.size(); j++) {
				if (doclist.get(j).getLatitudeid() == friList.get(i).getRuleid()) {
					treeDocrule.setSectionname(friList.get(i).getSectionname() + "," + doclist.get(j).getNums());
					break;
				}
			}
			treeDocrule.setRuleid(friList.get(i).getRuleid());
			lists.add(treeDocrule);
		}
		List<TreeDocrule> list = new ArrayList<TreeDocrule>();
		for (TreeDocrule tree : lists) {
			if (tree.getFid() == 0) {
				list.add(tree);
			}
			for (TreeDocrule t : lists) {
				//用t.getFid() == tree.getRuleid() 做条件时 会出现值相等但是表达式结果为false  
				//integer -128 ~ 127 存放在常量池中   == 比较的是地址值
				if ((t.getFid()).equals((tree.getRuleid()))) {
					if (tree.getChildren() == null) {
						List<TreeDocrule> myChildrens = new ArrayList<TreeDocrule>();
						myChildrens.add(t);
						tree.setChildren(myChildrens);
					} else {
						tree.getChildren().add(t);
					}
				}
			}
		}
		Collections.sort(list, new Comparator<TreeDocrule>() {
            @Override
            public int compare(TreeDocrule o1, TreeDocrule o2) {
                return o1.getRuleid().compareTo(o2.getRuleid());
            }
        });
		return list;
	}

	public Map objByMap(Object object) {
		Map map = new HashMap();
		JSONObject jsonObject = JSONObject.fromObject(object);
		map.put("ruleid", jsonObject.get("ruleid"));
		map.put("fid", jsonObject.get("fid"));
		map.put("sectionname", jsonObject.get("sectionname"));
		map.put("rule", jsonObject.get("rule"));
		map.put("reserved", jsonObject.get("reserved"));
		return map;
	}

	public List<TreeDocrule> treeList(int latitudeid) {
		List<Docrule> nextSubSet = new ArrayList<Docrule>();
		TreeDocrule voteTree = new TreeDocrule();
		voteTree.setRuleid(latitudeid);
		nextSubSet = docRuleDao.getNextSubSet(voteTree);
		List<TreeDocrule> list = new ArrayList<TreeDocrule>();
		for (int i = 0; i < nextSubSet.size(); i++) {
			// 遍历这个二级目录的集合
			TreeDocrule treelatitude = new TreeDocrule();
			treelatitude.setRuleid(nextSubSet.get(i).getRuleid());
			treelatitude.setFid(nextSubSet.get(i).getFid());
			treelatitude.setSectionname(nextSubSet.get(i).getSectionname());
			List<TreeDocrule> ts = docRuleDao.getDeeptLevel(nextSubSet.get(i));
			// 将下面的子集都依次递归进来
			treelatitude.setChildren(ts);
			list.add(treelatitude);
		}
		return list;
		// List<TreeDocrule> nextSubSet = new ArrayList<TreeDocrule>();
		// TreeDocrule voteTree = new TreeDocrule();
		// voteTree.setRuleid(latitudeid);
		// nextSubSet = docRuleDao.getNextSubSet(voteTree);
		// return nextSubSet;
	}

	@Override
	public Map<?, ?> getDcoSection(Docrule docrule) {
		Map<?, ?> map = docRuleDao.getDcoSection(docrule);
		return map;
	}

	@Override
	public void saveyldelete(String sectionname, User user) {
		docSectionAndRuleDao.saveyldelete(sectionname, user);

	}

	@Override
	public void saveyl(Docsectionandrule docsectionandrule) {
		docSectionAndRuleDao.saveyl(docsectionandrule);
	}

	@Override
	public List<Map> getSecNameShow(String sectionname) {
		List<Docrule> list = docRuleDao.getSecNameShow(sectionname);
		List<Map> lists = new ArrayList<Map>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> treeMap = new HashMap<String, Object>();
			treeMap.put("ruleid", list.get(i).getRuleid());
			treeMap.put("fid", list.get(i).getFid());
			treeMap.put("sectionname", list.get(i).getSectionname());
			treeMap.put("children", treeList(list.get(i).getRuleid()));
			lists.add(treeMap);
		}
		return lists;
	}

	@Override
	public List<Docrule> getRuleShow(Integer ruleid, String causeo, String causet, String spcx, String doctype) {
		List<Docrule> list = docRuleDao.getRuleShow(ruleid, causeo, causet, spcx, doctype);
		return list;
	}

	@Override
	public String updatesecname(Docrule docrule) {
		String msg = docRuleDao.updatesecname(docrule);
		return msg;
	}
}
