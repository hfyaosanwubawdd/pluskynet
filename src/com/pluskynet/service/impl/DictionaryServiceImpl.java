package com.pluskynet.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pluskynet.dao.DictionaryDao;
import com.pluskynet.dao.DictionaryHisDao;
import com.pluskynet.domain.Dictionary;
import com.pluskynet.domain.DictionaryHis;
import com.pluskynet.domain.User;
import com.pluskynet.otherdomain.Dictionarytree;
import com.pluskynet.service.DictionaryService;

public class DictionaryServiceImpl implements DictionaryService {
	private DictionaryDao dictionaryDao;

	public void setDictionaryDao(DictionaryDao dictionaryDao) {
		this.dictionaryDao = dictionaryDao;
	}

	private DictionaryHisDao dictionaryHisDao;

	public void setDictionaryHisDao(DictionaryHisDao dictionaryHisDao) {
		this.dictionaryHisDao = dictionaryHisDao;
	}

	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

	@Override
	public List<Map> getDicList() {
		//父id为0的所有记录
		List<Dictionarytree> firstdic = dictionaryDao.first();
		List<Map> list = new ArrayList<Map>();
		for (int i = 0; i < firstdic.size(); i++) {
			Map<String, Object> treeMap = new HashMap<String, Object>();
			treeMap.put("id", firstdic.get(i).getId());
			treeMap.put("fid", firstdic.get(i).getFid());
			treeMap.put("name", firstdic.get(i).getName());
			treeMap.put("children", treeList(firstdic.get(i).getId())/** 添加子节点 目录 **/);
			list.add(treeMap);
		}

		return list;

	}

	private Object treeList(int id) {
		List<Dictionarytree> nextSubSet = new ArrayList<Dictionarytree>();
		Dictionarytree voteTree = new Dictionarytree();
		voteTree.setId(id);
		nextSubSet = dictionaryDao.getNextSubSet(voteTree);
		return nextSubSet;
	}

	@Override
	public String addDic(Dictionary dictionary, User user) {
		String msg = dictionaryDao.addDic(dictionary);
		if (msg.equals("成功")) {
			DictionaryHis dictionaryHis = new DictionaryHis();
			dictionaryHis.setName(dictionary.getName());
			dictionaryHis.setFid(dictionary.getFid());
			dictionaryHis.setBelonguser(user.getUsername());
			dictionaryHis.setBelongid(user.getUserid()); 
			dictionaryHis.setBelongtime(df.format(new Date()));
			dictionaryHisDao.save(dictionaryHis);
		}
		return msg;
	}

	@Override
	public String updateDic(Dictionary dictionary, User user) {
		String msg = dictionaryDao.updateDic(dictionary);
		if (msg.equals("成功")) {
			DictionaryHis dictionaryHis = new DictionaryHis();
			dictionaryHis.setName(dictionary.getName());
			dictionaryHis.setFid(dictionary.getFid());
			dictionaryHis.setBelonguser(user.getUsername());
			dictionaryHis.setBelongid(user.getUserid()); 
			dictionaryHis.setBelongtime(df.format(new Date()));
			dictionaryHisDao.save(dictionaryHis);
		}
		return msg;
	}

	@Override
	public String deleteDic(Dictionary dictionary) {
		String msg = dictionaryDao.deleteDic(dictionary);
		return msg;
	}

	@Override
	public List<Map> getDicname(String code) {
		List<Dictionarytree> firstdic = dictionaryDao.getDicname(code);
		List<Map> list = new ArrayList<Map>();
		for (int i = 0; i < firstdic.size(); i++) {
			Map<String, Object> treeMap = new HashMap<String, Object>();
			treeMap.put("id", firstdic.get(i).getId());
			treeMap.put("fid", firstdic.get(i).getFid());
			treeMap.put("name", firstdic.get(i).getName());
			treeMap.put("code", firstdic.get(i).getCode());
			list.add(treeMap);
		}
		return list;
	}

	@Override
	public List<Map> getNextdic(Integer id) {
		List<Dictionary> listDic = dictionaryDao.getNextdic(id);
		List<Map> list = new ArrayList<Map>();
		for (int i = 0; i < listDic.size(); i++) {
			Map<String, Object> treeMap = new HashMap<String, Object>();
			treeMap.put("id", listDic.get(i).getId());
			treeMap.put("fid", listDic.get(i).getFid());
			treeMap.put("name", listDic.get(i).getName());
//			treeMap.put("children", treeList(firstdic.get(i).getId()));
			list.add(treeMap);
		}
		return list;
	}

	@Override
	public List<Map> getCodedic(String code) {
		List<Dictionary> listDic = dictionaryDao.getCodedic(code);
		List<Map> list = new ArrayList<Map>();
		for (int i = 0; i < listDic.size(); i++) {
			Map<String, Object> treeMap = new HashMap<String, Object>();
			treeMap.put("id", listDic.get(i).getId());
			treeMap.put("fid", listDic.get(i).getFid());
			treeMap.put("name", listDic.get(i).getName());
//			treeMap.put("children", treeList(firstdic.get(i).getId()));
			list.add(treeMap);
		}
		return list;
	}
}
