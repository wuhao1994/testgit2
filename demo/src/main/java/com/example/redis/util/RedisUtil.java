package com.example.redis.util;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

public  class RedisUtil {
	@Resource 
	private static RedisTemplate redisTemplate;
    public static Object mysqlgetById(String className,String id){
    	HashOperations<String,Long,Object> operations=redisTemplate.opsForHash();
    	return(operations.get(className, id));
    }
}
