package com.cyberbay.frog.pay.common.util;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class JsonUtil {
	private static JsonConfig jsonConfig = new JsonConfig();
	static{
		DateJsonValueProcessor beanProcessor = new DateJsonValueProcessor(  
        		DateJsonValueProcessor.DEFAULT_DATE_PATTERN);  
        jsonConfig.registerJsonValueProcessor(Date.class, beanProcessor);
	}
	/**
	 * @method: toJSONString
	 * @Description: 对象转换成Json串
	 * @param obj 需要转换的json串
	 * @return json串
	 */
	public static String toJSONString(Object obj){
        JSONObject jsonObj = JSONObject.fromObject(obj,jsonConfig);
		return jsonObj.toString();
	}
	/**
	 * @method: toObject
	 * @Description: JSON字符串转换成对象 
	 * @param jsonString 需要转换的字符串 
	 * @param type 需要转换的对象类型 
	 * @return 对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toObject(String jsonString, Class<T> type) {
        JSONObject jsonObject = JSONObject.fromObject(jsonString,jsonConfig);
        return (T) JSONObject.toBean(jsonObject, type);
	}
	
	/**
	 * @method: toJSONString
	 * @Description: 集合转换成Json串
	 * @param list 需要转换的集合
	 * @return json串
	 */
	public static <T> String toJSONString(List<T> list){
        JSONArray jsonArray = JSONArray.fromObject(list,jsonConfig);
		return jsonArray.toString();
	}
	
	/**
	 * @method: mapToJSONString
	 * @Description: Map转换成Json串
	 * @param map 需要转换的map
	 * @return json串
	 */
	@SuppressWarnings("rawtypes")
	public static String toJSONString(Map map){
		JSONObject jsonObject = JSONObject.fromObject(map,jsonConfig);
		return jsonObject.toString();
	}
	
	/**
	 * @method: mapToReturnString
	 * @Description: 组装返回数据
	 * @param map 需要组装的map
	 * @return 返回组装后的json
	 */
	public static String toReturnString(Map<String,Object> map){
		JSONObject jsonObject = Single.getJsonObject();
		for(Map.Entry<String, Object> entry : map.entrySet()){
			String key = entry.getKey();
			Object object = entry.getValue();
			if(object instanceof String || object instanceof Integer || object instanceof Long){
				jsonObject.put(key, object.toString());
			}else{
				jsonObject.put(key, toJSONString(object));
			}
			
		}
		return jsonObject.toString();
	}
	
	/**
	 * @method: toJSONArray
	 * @Description: 将对象转换为JSON对象数组
	 * @param object 对象
	 * @return JSON对象数组
	 */
	public static JSONArray toJSONArray(Object object){
		return JSONArray.fromObject(object,jsonConfig);
	}
	
	/**
	 * @method: toJSONObject
	 * @Description: 将对象转换为JSON对象
	 * @param object 对象
	 * @return JSON对象
	 */
	public static JSONObject toJSONObject(Object object)
	{
		return JSONObject.fromObject(object,jsonConfig);
	}
	
	/**
	 * @method: toHashMap
	 * @Description: 将对象转换为HashMap
	 * @param object 对象
	 * @return HashMap
	 */
	@SuppressWarnings("rawtypes")
	public static HashMap toHashMap(Object object){
		HashMap<String, Object> data = new HashMap<String, Object>();
		JSONObject jsonObject = toJSONObject(object);
		Iterator it = jsonObject.keys();
		while (it.hasNext())
		{
			String key = String.valueOf(it.next());
			Object value = jsonObject.get(key);
			data.put(key, value);
		}

		return data;
	}
	
	/**
	 * @method: toList
	 * @Description: 将JSON对象数组转换为传入类型的List
	 * @param jsonArray 将JSON对象数组
	 * @param objectClass 传入类型的List
	 * @return list
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static <T> List<T> toList(JSONArray jsonArray, Class<T> objectClass)
	{
		return JSONArray.toList(jsonArray, objectClass);
	}
	
	/**
	 * @method: toBean
	 * @Description: 将JSON对象转换为传入类型的对象
	 * @param jsonObject JSON对象
	 * @param beanClass 传入类型的对象
	 * @return 传入类型的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toBean(JSONObject jsonObject, Class<T> beanClass)
	{
		return (T) JSONObject.toBean(jsonObject, beanClass);
	}
	
	/**
	 * @method: outPutJson
	 * @Description: 封装json数据从后台传输
	 * @param response 
	 * @param obj 
	 */
	public static void outPutJson(HttpServletResponse response,Object obj){
		try {
			response.setContentType("text/html");
			//config this from nginx server
			//response.setHeader("Access-Control-Allow-Origin", "*");
			response.getWriter().print(obj);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
//		Thuser user = new Thuser();
//		user.setId(1L);
//		user.setName("李建军");
//		user.setSex("000");
//		
//		Thuser user1 = new Thuser();
//		user1.setId(2L);
//		user1.setName("李建军2");
//		user1.setSex("0002");
//		
//		List<Thuser> list = new ArrayList<>();
//		list.add(user);
//		list.add(user1);
//		
//		// 一级json
//		Map map = new HashMap<>();
//		map.put("a", "A");
//		map.put("b", "B");
//		map.put("c", "C");
//		// 二级json
//		Map mapData = new HashMap<>();
//		mapData.put("classification1Infos", listToJSONString(list));
//		map.put("data", mapData);
//		// 输出格式
//		System.out.println(mapToReturnString(map));
//		Map map = new HashMap<>();
//		map.put("a", new Date());
//		map.put("b", "B");
//		map.put("c", "C");
//		System.out.println(toJSONString(map));
	}
}
