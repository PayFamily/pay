package com.cyberbay.frog.pay.alipay.service.impl;





import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberbay.frog.pay.alipay.dao.UserMapper;
import com.cyberbay.frog.pay.alipay.entity.User;
import com.cyberbay.frog.pay.alipay.service.UserService;


@Service("userService")
public class UserServiceImpl  implements UserService {
	@Autowired
	UserMapper userMapper;
	public User say(String name) {
		System.out.println(new Date()+name);
		User user =userMapper.findUserByName("张三");
		System.out.println(user);
		return user;
	}

	

	

}
