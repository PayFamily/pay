package com.cyberbay.frog.pay.common.web.controller;

import org.apache.log4j.Logger;


/**
* @Description: web层基础类
* 

*/
public class BaseController{
//public class BaseAction  extends ActionSupport{
	protected transient final Logger log = Logger.getLogger(this.getClass());

	public static final String  STATUS = "status";

	public static final String  MSG = "msg";
	
	public static final String  DATA = "data";
	
	
	public BaseController() {
	}
	
}
