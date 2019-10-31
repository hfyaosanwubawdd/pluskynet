package com.pluskynet.test.my;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReentrantLock;

/**
* @author HF
* @version 创建时间：2019年10月25日 下午2:29:30
* 类说明
*/
public class ThreadCommunicate {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Callable<Integer> callable = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("sdf~~~~~~~~~~~~~~~~~~~safsaf");
				Thread.sleep(5000);
				System.out.println("!!!!!!!!!!!!!!!!!!!!");
				return 100;
			}
		};
		FutureTask<Integer> future = new FutureTask<Integer>(callable);
		new Thread(future).start();
//		System.out.println(future.get());
		Thread.sleep(7000);
		System.out.println("asfsadfsfd");
	}
}
