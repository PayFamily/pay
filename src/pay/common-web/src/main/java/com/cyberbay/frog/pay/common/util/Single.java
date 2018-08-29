package com.cyberbay.frog.pay.common.util;

import net.sf.json.JSONObject;

public class Single{
	
	// json静态对象
	private static final JSONObject jsonObject = new JSONObject();
	
	public Single(){}
	
	/**
	 * @method: getJsonObject
	 * @Description: 获取json对象
	 * @return 返回json对象
	 */
    public static JSONObject getJsonObject() {  
        return jsonObject;
    }

}
