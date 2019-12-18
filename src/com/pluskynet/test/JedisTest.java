package com.pluskynet.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

import com.pluskynet.domain.Cause;
import com.pluskynet.util.JedisUtils;

import redis.clients.jedis.Jedis;

/**
* @author HF
* @version 创建时间：2019年11月26日 下午3:26:00
* 类说明
*/
public class JedisTest {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss");
	public static void main(String[] args) {
		for (int i = 0; i < 11; i++) {
			String key = "incrtest_"+sdf.format(new Date());
			long incrCount = JedisUtils.incrCount(key);
			if (incrCount == 1) {
				JedisUtils.expire(key, 1);
			}
			System.out.println(incrCount);
		}
	}
}
