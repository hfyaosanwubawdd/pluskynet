package com.pluskynet.service;

import java.util.List;

import com.pluskynet.domain.Sample;
import com.pluskynet.domain.User;

public interface SampleService extends BaseService{

	void random(Sample sample, User user,int type);
	void randomNew(Sample sample,User user,int type);
	List<Sample> select(User user, int type);
	void deleteSample(Sample sample);
}
