package com.cyberbay.frog.pay.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class DateJsonValueProcessor implements JsonValueProcessor {
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";  
    private DateFormat dateFormat;  
  
    /**   
     * 构造方法.   
     *   
     * @param datePattern 日期格式   
     */  
    public DateJsonValueProcessor(String datePattern) {  
        try {  
            dateFormat = new SimpleDateFormat(datePattern);  
        } catch (Exception ex) {  
            dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);  
        }  
    }  
  
    public Object processArrayValue(Object value, JsonConfig jsonConfig) {  
        return process(value);  
    }  
  
    public Object processObjectValue(String key, Object value,  
            JsonConfig jsonConfig) {  
        return process(value);  
    }  
  
    private Object process(Object value) {  
        if (value == null) {  
//            value = new Date();   //为null时返回当前日期，也可以返回"",看需要  
        	return null;
        }  
        return dateFormat.format((Date) value);  
    }  
  
    @SuppressWarnings("rawtypes")
	public static String map2JSonStr(Map map) {  
        JsonConfig jsonConfig = new JsonConfig();  
        DateJsonValueProcessor beanProcessor = new DateJsonValueProcessor(  
                DEFAULT_DATE_PATTERN);  
        jsonConfig.registerJsonValueProcessor(Date.class, beanProcessor);  
  
        JSONObject jsonObject = JSONObject.fromObject(map, jsonConfig);  
  
        return jsonObject.toString();  
    }     
      
    //将对象转换为json string，使用上面定义的的日期格式  
    public static JSONObject obj2JsonObj(Object obj) {  
        JsonConfig jsonConfig = new JsonConfig();  
        DateJsonValueProcessor beanProcessor = new DateJsonValueProcessor(  
                DEFAULT_DATE_PATTERN);  
        jsonConfig.registerJsonValueProcessor(Date.class, beanProcessor);  
  
        JSONObject jsonObject = JSONObject.fromObject(obj, jsonConfig);  
  
        return jsonObject;  
    }  
}
