package com.pluskynet.dao;



import java.util.List;
import java.util.Map;

import com.pluskynet.domain.Docrule;
import com.pluskynet.otherdomain.TreeDocrule;
import com.pluskynet.otherdomain.Treelatitude;
@SuppressWarnings("all")
public interface DocRuleDao {

	Map save(Docrule docrule,Integer type);

	String update(Docrule docrule);

	List<Docrule> getDcoSectionList(Integer type);

	List<Docrule> getNextSubSet(TreeDocrule voteTree);

	List<Docrule> getSecNameShow(String sectionname);

	List<Docrule> getRuleShow(Integer ruleid, String causeo, String causet, String spcx, String doctype);

	String updatesecname(Docrule docrule);

	List<TreeDocrule> getDeeptLevel(Docrule treeDocrule);

	Map getDcoSection(Docrule docrule);

}
