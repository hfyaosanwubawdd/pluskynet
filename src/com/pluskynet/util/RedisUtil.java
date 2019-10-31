package com.pluskynet.util;
import redis.clients.jedis.Jedis;
public class RedisUtil {  
private final static String redisAddress = "127.0.0.1";
private final static Jedis jedis = new Jedis(redisAddress);  
    /**
     *  得到redis数据
     * @param keys
     * @return object
     */
    public static Object getRedisValue(String keys) {  
    
    // 取数据  
    return jedis.get(keys);
    } 
 
    public static void setRedisValue(String key,String obj){
    jedis.del(key);
    jedis.set(key,obj);
    }
    /**
     * 向redis数据库中写入和读取数据
     */
    public static void main(String[] args) {
        RedisUtil.setRedisValue("name", "风云");
        Object name= RedisUtil.getRedisValue("name");
        System.out.println(name.toString());	
	}
}