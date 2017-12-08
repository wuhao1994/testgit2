package com.example.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SpringData.dao.UserRepository;
import com.example.SpringData.domain.User;

/**
 * Created by Brave on 16/10/9.
 */
@RestController
public class HelloController {
	@Resource
	private UserRepository userRepository;
    @RequestMapping("/hello")
    public String index() {
        return "Hello World123456";
    }
    
    @RequestMapping("/saveUser")
    public void saveUser(User user) {
    	userRepository.save(user);
        System.out.println("存成功了");  
    }
    
    @RequestMapping("/getUser")
    @Cacheable(value="user-key")
    public User getUser() {
    	userRepository.save(new User("aaa",12));
        User user=userRepository.findByName("aa");
        System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");  
        return user;
    }
}
