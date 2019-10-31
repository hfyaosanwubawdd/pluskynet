package com.pluskynet.test.my;
/**
* @author HF
* @version 创建时间：2019年10月21日 下午4:27:50
* 类说明 公平锁 非公平锁
*/

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class FairLock {

	private ReentrantLock lock;
	public FairLock(boolean isFair) {
		super();
		lock = new ReentrantLock(isFair);
	}
	public void method() {
		try {
			lock.lock();
			System.out.println("ThreadName=" + Thread.currentThread().getName()+ "获取锁");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {lock.unlock();}
	}
	
	public static void main(String[] args) {
		FairLock fair = new FairLock(true);//公平锁
//		FairLock fair = new FairLock(false);//非公平锁
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				System.out.println("★线程" + Thread.currentThread().getName()+ "运行了");
				fair.method();
			}
		};
		List<Thread> threadList = new ArrayList<Thread>();
		Thread[] threadArr = new Thread[10];
		for (int i = 0; i < threadArr.length; i++) {
			threadArr[i] = new Thread(runnable);
		}
		
		for (Thread thread : threadArr) {
			thread.start();
		}
	}
}
