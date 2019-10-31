package com.pluskynet.service;

import java.util.List;
import java.util.Map;

import com.pluskynet.domain.Docsectionandrule;
import com.pluskynet.domain.Latitude;
import com.pluskynet.domain.StatsDoc;
import com.pluskynet.domain.User;
import com.pluskynet.otherdomain.Treelatitude;
@SuppressWarnings("all")
public interface LatitudeService {

	Map save(Latitude latitude, User user, int type);

	String update(Latitude latitude, User user);

	List<Map> getLatitudeList(User user, int type);

	List<Treelatitude> treeList(int latitudeid,User user);

	Latitude getLatitude(Latitude latitude);

	List<String> getLatitudeName(Latitude latitude);

	List<Map> getScreeList(String latitudeName,Integer latitudeId);

	List<Map> getLatitudeShow(String latitudename, User user);

	List<Latitude> getRuleShow(Integer latitudeid, String cause, String spcx, String sectionname);

	String updateName(Latitude latitude, User user);

	String approve(Latitude latitude, User user);

	List<StatsDoc> getDocList(Latitude latitude, User user, int type, List<Docsectionandrule> list, String batchno);

}
