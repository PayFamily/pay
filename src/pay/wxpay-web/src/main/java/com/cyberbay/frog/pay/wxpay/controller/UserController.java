package com.cyberbay.frog.pay.wxpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyberbay.frog.pay.wxpay.entity.User;
import com.cyberbay.frog.pay.wxpay.service.UserService;




@Controller
@Scope("prototype")
public class UserController {

	@Autowired
	UserService userService;

	@RequestMapping(value = "/aa", method = RequestMethod.GET)
	@ResponseBody
	public User register(String name) {
		String str = "";
		str = str + name;
		User user= userService.say(name);
		// return "redirect:/index.jsp";
		return user;
	}
}
