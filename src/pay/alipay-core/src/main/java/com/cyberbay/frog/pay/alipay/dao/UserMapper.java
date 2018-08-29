package com.cyberbay.frog.pay.alipay.dao;

import com.cyberbay.frog.pay.alipay.entity.User;

public interface UserMapper {
	User findUserByName(String name);
}
