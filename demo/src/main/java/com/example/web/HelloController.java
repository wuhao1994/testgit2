package com.example.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

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
    	operations.putIfAbsent(User.class.toString(),u.getId().toString(),SerializeUtil.serialize(u));
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
    	User user = (User)RedisUtil.mysqlgetById(User.class.toString(), id.toString());
    	if(user ==null){
    		user = mysqlgetUserById(id);
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
}
