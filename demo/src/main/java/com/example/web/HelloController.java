package com.example.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SpringData.dao.UserRepository;
import com.example.SpringData.domain.User;
import com.example.redis.util.RedisUtil;
import com.example.redis.util.SerializeUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Brave on 16/10/9.
 */
@RestController
public class HelloController {
	@Resource
	private UserRepository userRepository;
	@Autowired
    private RedisTemplate redisTemplate;
    @RequestMapping("/hello")
    public String index() {
        return "Hello World123456";
    }
    
    @RequestMapping("/saveUser")
    public void saveUser(User user) {
    	User u = userRepository.save(user);
    	HashOperations<String,String,Object> operations=redisTemplate.opsForHash();
    	ObjectMapper objectmapper = new ObjectMapper();
    	try {
			String userStr = objectmapper.writeValueAsString(u);
			Map<String,String> hash = new LinkedHashMap<String,String>();
			hash.put(u.getId().toString(), userStr);
	    	operations.putAll(User.class.toString(), hash);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("存成功了");  
    }
    
    @RequestMapping("/getUser")
    @Cacheable(value="user-key")
    public User getUser() {
    	User user = mysqlgetUserByName();
        return user;
    }
    @RequestMapping("/getUserById")
    public User getUserById(Long id) {
    	ObjectMapper objectmapper = new ObjectMapper();
		String uStr = (String) mysqlgetById(User.class.toString(), id.toString());
		User user = new User();
		if(uStr ==null){
    		user = mysqlgetUserById(id);
    	}else{
    		try {
    			user = objectmapper.readValue(uStr, User.class);
    		} catch (JsonParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (JsonMappingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	
    	}
        return user;
    }
    public User mysqlgetUserByName(){
    	 User user=userRepository.findByName("aa");
         System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");  
         return user;
    }
    public User mysqlgetUserById(Long id){
   	 User user=userRepository.findOne(id);
        System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");  
        return user;
   }
    public User redisgetUserById(Long id){
      	 User user=userRepository.findOne(id);
           System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");  
           return user;
      }
    @RequestMapping("/getUsers")
    @Cacheable(value="key-Users")
    public List<User> getUsers() {
    	List<User> users=userRepository.findAll();
    	System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");  
        return users;
    }
    
    public Object mysqlgetById(String className,String id){
    	HashOperations<String,Long,Object> operations=redisTemplate.opsForHash();
    	return(operations.get(className, id));
    }
    
    @RequestMapping("/uid")
    //sesionid 做键  uid做值 key如spring:session:sessions:ff325db0-a88b-4510-8fee-d65266980219
    String uid(HttpSession session) {
        UUID uid = (UUID) session.getAttribute("uid");
        if (uid == null) {
            uid = UUID.randomUUID();
        }
        session.setAttribute("uid", uid);
        return session.getId();
    }
}
