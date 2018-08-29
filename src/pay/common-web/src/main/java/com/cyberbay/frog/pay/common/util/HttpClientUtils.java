package com.cyberbay.frog.pay.common.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * http 请求
 * @Description: TODO
 * @Company:TGRF
 * @author:lig
 * @date: 2015年12月3日 下午2:18:58
 * @version V1.0
 */
public class HttpClientUtils {
	
	/**
	 * post请求
	 * @method: postHttpsByUrl
	 * @Description: TODO
	 * @param url
	 * @return
	 * @author: lig
	 * @date 2015年12月3日 下午2:19:09
	 */
	public static String postHttpsByUrl(String url){
		
	     HttpClient httpClient = new HttpClient();//构造HttpClient的实例
	     httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(300000);//设置 Http 连接超时
	     //String url = "http://192.168.1.237:9000/solr/clustering?q=name%3A*&wt=html&indent=true&encoding=utf-8";
	     PostMethod postMethod = new PostMethod(url);//创建Post方法的实例
	     //getMethod.getParams().setContentCharset("GB2312");
	     postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,300000);//设置 post 请求超时
	     
	     postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());//使用系统提供的默认的恢复策略
	     try{
	    	
		      //执行postMethod
		      int statusCode = httpClient.executeMethod(postMethod);
		      if (statusCode != HttpStatus.SC_OK){
		    	  System.err.println("Method failed: "+ postMethod.getStatusLine());
		      }
		      
		      //读取内容 ,第二种方式获取
		      String newStr = null;
		     // String os = System.getProperty("os.name");
		     // System.out.println(os);
		    /*  if (os != null && os.startsWith("Windows")) { 
		    	  newStr = new String(getMethod.getResponseBodyAsString().getBytes(),"GB2312");
		      }else {
		    	  newStr = new String(getMethod.getResponseBodyAsString().getBytes(),"UTF-8");
		      }*/
		      newStr = new String(postMethod.getResponseBodyAsString().getBytes(),"UTF-8");
		      //System.out.println(newStr);
		      return new String(newStr);
		      
		      //读取内容 ,第一种方式获取
		      //byte[] responseBody = getMethod.getResponseBody();
		      //System.out.println(new String(responseBody));//处理内容
		      
	     }catch(HttpException e){
	    	 //发生致命的异常，可能是协议不对或者返回的内容有问题
	    	 System.out.println("Please check your provided http address!");
	    	 e.printStackTrace();
	     }catch(IOException e){
	    	 //发生网络异常
	    	 e.printStackTrace();
	     }finally{
	    	 //释放连接
	    	 postMethod.releaseConnection();
	     }
		return null;
	}
	
	public static String sendXMLDataByPost(String url, String xmlData) {
		HttpClient httpClient = new HttpClient();// 构造HttpClient的实例
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(300000);// 设置
		String responseBody = "";																			// Http
																						// 连接超时
		// String url =
		// "http://192.168.1.237:9000/solr/clustering?q=name%3A*&wt=html&indent=true&encoding=utf-8";
		PostMethod postMethod = new PostMethod(url);// 创建Post方法的实例
		// Send data by post method in HTTP protocol,use HttpPost instead of
		// PostMethod which was occurred in former version
		// Construct a string entity
		postMethod.setRequestBody(xmlData);
		// Set XML entity
		postMethod.setRequestHeader("Content-type", "text/xml; charset=utf-8");
		postMethod.setRequestHeader("charset","utf-8");  
		int result;
		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
			result = httpClient.executeMethod(postMethod);
			System.out.println("Response status code: " + result);// 返回200为成功
			System.out.println("Response body: ");
			System.out.println(postMethod.getResponseBodyAsString());// 返回的内容
			
           if(result == HttpStatus.SC_OK){    
                bis = new BufferedInputStream(postMethod.getResponseBodyAsStream());    
                byte[] bytes = new byte[1024];    
                bos = new ByteArrayOutputStream();    
                int count = 0;    
                while((count = bis.read(bytes))!= -1){    
                    bos.write(bytes, 0, count);    
                }    
                byte[] strByte = bos.toByteArray();    
                responseBody = new String(strByte,0,strByte.length,"utf-8");    
 
            }    
			
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			postMethod.releaseConnection();// 释放连接
            try {
				bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
            try {
				bis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		}
		return responseBody;
	}

	
	public static String getHttpsByUrl(String url,NameValuePair[] param){
		
	     HttpClient httpClient = new HttpClient();//构造HttpClient的实例
	     httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(300000);//设置 Http 连接超时
	     GetMethod getMethod = new GetMethod(url);//创建Post方法的实例
	     
	     //设置参数
	     getMethod.setQueryString(param);
	     
	     getMethod.getParams().setContentCharset("utf-8");
	     getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,300000);//设置 post 请求超时
	     
	     getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());//使用系统提供的默认的恢复策略
	     try{
	    	 	
	    	 
		      //执行postMethod
		      int statusCode = httpClient.executeMethod(getMethod);
		      if (statusCode != HttpStatus.SC_OK){
		    	  System.err.println("Method failed: "+ getMethod.getStatusLine());
		      }
		      
		      //读取内容 ,第二种方式获取
		      String newStr = null;
		     // String os = System.getProperty("os.name");
		     // System.out.println(os);
		    /*  if (os != null && os.startsWith("Windows")) { 
		    	  newStr = new String(getMethod.getResponseBodyAsString().getBytes(),"GB2312");
		      }else {
		    	  newStr = new String(getMethod.getResponseBodyAsString().getBytes(),"UTF-8");
		      }*/
		      newStr = new String(getMethod.getResponseBodyAsString().getBytes(),"UTF-8");
		      //System.out.println(newStr);
		      return getMethod.getResponseBodyAsString();
		      
		      //读取内容 ,第一种方式获取
		      //byte[] responseBody = getMethod.getResponseBody();
		      //System.out.println(new String(responseBody));//处理内容
		      
	     }catch(HttpException e){
	    	 //发生致命的异常，可能是协议不对或者返回的内容有问题
	    	 System.out.println("Please check your provided http address!");
	    	 e.printStackTrace();
	     }catch(IOException e){
	    	 //发生网络异常
	    	 e.printStackTrace();
	     }finally{
	    	 //释放连接
	    	 getMethod.releaseConnection();
	     }
		return null;
	}
	
	public static String postHttpsByUrl(String url,NameValuePair[] param){
		
	     HttpClient httpClient = new HttpClient();//构造HttpClient的实例
	     httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(300000);//设置 Http 连接超时
	     PostMethod postMethod = new PostMethod(url);//创建Post方法的实例
	     
	     //设置参数
	     postMethod.setRequestBody(param);
	     
	     postMethod.getParams().setContentCharset("utf-8");
	     postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,300000);//设置 post 请求超时
	     
	     postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());//使用系统提供的默认的恢复策略
	     try{
	    	 	
	    	 
		      //执行postMethod
		      int statusCode = httpClient.executeMethod(postMethod);
		      if (statusCode != HttpStatus.SC_OK){
		    	  System.err.println("Method failed: "+ postMethod.getStatusLine());
		      }
		      
		      //读取内容 ,第二种方式获取
		      String newStr = null;
		     // String os = System.getProperty("os.name");
		     // System.out.println(os);
		    /*  if (os != null && os.startsWith("Windows")) { 
		    	  newStr = new String(getMethod.getResponseBodyAsString().getBytes(),"GB2312");
		      }else {
		    	  newStr = new String(getMethod.getResponseBodyAsString().getBytes(),"UTF-8");
		      }*/
		      newStr = new String(postMethod.getResponseBodyAsString().getBytes(),"UTF-8");
		      //System.out.println(newStr);
		      return new String(newStr);
		      
		      //读取内容 ,第一种方式获取
		      //byte[] responseBody = getMethod.getResponseBody();
		      //System.out.println(new String(responseBody));//处理内容
		      
	     }catch(HttpException e){
	    	 //发生致命的异常，可能是协议不对或者返回的内容有问题
	    	 System.out.println("Please check your provided http address!");
	    	 e.printStackTrace();
	     }catch(IOException e){
	    	 //发生网络异常
	    	 e.printStackTrace();
	     }finally{
	    	 //释放连接
	    	 postMethod.releaseConnection();
	     }
		return null;
	}
	
	
	
	
	//test
	public static void main(String[] args) {
	
	}
	
	
	
}
