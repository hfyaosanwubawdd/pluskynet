package com.pluskynet.batch.thread;

import com.pluskynet.service.BaseService;
import com.pluskynet.service.impl.SampleRandLaServiceImpl;
import com.pluskynet.service.impl.SampleRandServiceImpl;
import com.pluskynet.service.impl.SampleServiceImpl;
import com.pluskynet.service.impl.ViewDocServiceImple;
import com.pluskynet.service.impl.ViewHisServiceImpl;

/**
* @author HF
* @version 创建时间：2019年11月6日 下午2:37:34
* 类说明
*/
public class SamplesThread implements Runnable {
	private Integer sampleid;
	public SamplesThread() {
		super();
	}
	public SamplesThread(Integer sampleid) {
		super();
		this.sampleid = sampleid;
	}

	@Override
	public void run() {
		new SampleRandServiceImpl().deleBySampleid(sampleid);
	    new SampleRandLaServiceImpl().deleBySampleid(sampleid);
		new ViewHisServiceImpl().deleBySampleid(sampleid);
		new ViewDocServiceImple().deleBySampleid(sampleid);
	}

}
