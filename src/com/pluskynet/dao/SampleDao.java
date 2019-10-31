package com.pluskynet.dao;

import java.util.List;

import com.pluskynet.domain.Article01;
import com.pluskynet.domain.Docsectionandrule;
import com.pluskynet.domain.Docsectionandrule01;
import com.pluskynet.domain.Sample;
import com.pluskynet.domain.User;

public interface SampleDao {

	List<Article01> getListArticle(String table, String year, int count,String trialRound,String doctype,User user);

	void save(List<Article01> list,User user);

	void saverule(Sample sample,User user, int type);

	List<Sample> select(User user, int type);

	void delete(User user);

	void deleteDoc(User user);

	void saveDoc(List<Docsectionandrule01> list, User user);

	void deleteSample(Sample sample);

	void updateSample(Sample sample);

	List<Sample> findById(Sample sample);

}
