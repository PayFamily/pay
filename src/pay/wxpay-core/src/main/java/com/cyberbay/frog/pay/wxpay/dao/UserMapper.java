package com.cyberbay.frog.pay.wxpay.dao;

import com.cyberbay.frog.pay.wxpay.entity.User;

public interface UserMapper {
	User findUserByName(String name);
}
