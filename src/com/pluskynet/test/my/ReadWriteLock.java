package com.pluskynet.test.my;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

/**
* @author HF
* @version 创建时间：2019年10月21日 下午2:45:48
* 类说明  读写锁
*/

public class ReadWriteLock {
	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	private static ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
	
	private static void read() {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		for (int i = 0; i < 100; i++) {
			final int j = i;
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					readLock.lock();
					 try {
						 System.out.println("get readlock i = "+j);
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {readLock.unlock();}
				}
			});
		}
		threadPool.shutdown();
	}
	private static void write() {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				writeLock.lock();
				System.out.println("get writelock ...");
				try {
					Thread.sleep(11111);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally {writeLock.unlock();}
			}
		});
		threadPool.shutdown();
	}
	public static void main(String[] args) {
		write();
		read();
	}
}
