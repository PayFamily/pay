package com.cyberbay.frog.pay.alipay.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.internal.util.AlipaySignature;
import com.cyberbay.frog.pay.common.util.HttpClientUtils;
import com.cyberbay.frog.pay.common.util.JsonUtil;
import com.cyberbay.frog.pay.common.util.WebConstants;
import com.cyberbay.frog.pay.common.web.controller.BaseController;





@Controller
public class AlipayNotifyController extends BaseController{
	//订单状态 01 为未支付
	private static final String ORDER_STATUS01 = "01";
	//订单状态 02 为待发货
	private static final String ORDER_STATUS02 = "02";
	private String ebsysUrl;
	private String url_u;

	@Value("${alipay.public.key}")
	private String ALIPAY_PUBLIC_KEY;
	@Value("${alipay.charset}")
	private String ALIPAY_CHARSET;
	@Value("${alipay.sign.type}")
	private String ALIPAY_SIGN_TYPE;

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value="/alipayNotify",method=RequestMethod.POST)
	@ResponseBody	
	public void alipayNotify(
			HttpServletRequest request, 
			HttpServletResponse response
			){
		String uuid = UUID.randomUUID().toString();
		log.info("[AlipayNotifyController.AlipayNotify Start]:uuid = "+uuid+" ^Enterprise account application!");
		try {
			//获取支付宝POST过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map<String,String[]> requestParams = request.getParameterMap();
			for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				params.put(name, valueStr);
			}
			JSONObject  jasonObject = JSONObject.fromObject(params);
			log.info("[AlipayNotifyController.AlipayNotify jasonObject] = "+jasonObject.toString()+" ^Enterprise account application!");
			boolean signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, ALIPAY_CHARSET, ALIPAY_SIGN_TYPE); //调用SDK验证签名

			if(signVerified) {//验证成功
				log.info("-------signVerified:"+signVerified);
				//商户订单号
				String outTradeNo = request.getParameter("out_trade_no").toString();

				//支付宝交易号
				//String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

				//交易状态
				String tradeStatus = request.getParameter("trade_status").toString();

				if(tradeStatus.equals("TRADE_FINISHED")||tradeStatus.equals("TRADE_SUCCESS")){
					//查询订单状态
					NameValuePair[] paramUrl = {
							new NameValuePair("orderId",outTradeNo),
							new NameValuePair("delFlg","0")
					};
					String orderResult = HttpClientUtils.getHttpsByUrl(ebsysUrl+"getOrder.do", paramUrl);
					JSONObject orderJson = JSONObject.fromObject(orderResult);
					String status = (String) orderJson.get("status");
					if(status.equals(WebConstants.STATUS_SUCCESS)){
						JSONArray orderArray =orderJson.getJSONArray("data");
						JSONObject oderObj = orderArray.getJSONObject(0);
						String orderStatus = oderObj.get("orderStatus").toString();
						//用户ID
						String userId = oderObj.getString("userId");
						//返券数
						Integer  commodityReturnTmoney = oderObj.getInt("commodityReturnTmoney");
						//使用的福利券
						Integer  tMoneyUse = oderObj.getInt("tMoneyUse");
						//额外返券数
						Integer  additionalReturnTmoney = oderObj.getInt("additionalReturnTmoney");
						//更新后的福利券
						Integer  updTMoney = 0;  
						//查询用户T币
						NameValuePair[] paramUrl1 = {
								new NameValuePair("userId",userId)
						};
						String tMoneyResult = HttpClientUtils.getHttpsByUrl(url_u+"searchUserTmoney.do",paramUrl1);
						this.log.info("-------tMoneyResult:"+tMoneyResult);
						JSONObject tMoneyJson = JSONObject.fromObject(tMoneyResult);
						this.log.info("-------tMoneyJson:"+tMoneyJson);
						String tMoney =tMoneyJson.get("data").toString();
						Integer tm = Integer.parseInt(tMoney);
						if(ORDER_STATUS01.equals(orderStatus)){
							NameValuePair[] param = {
									new NameValuePair("orderId",outTradeNo),
									new NameValuePair("orderStatus",ORDER_STATUS02)
							};
							HttpClientUtils.getHttpsByUrl(ebsysUrl+"updateOrder.do", param);
							updTMoney=tm-tMoneyUse+commodityReturnTmoney+additionalReturnTmoney;
							NameValuePair[] paramUrl2 = {
									new NameValuePair("userId",userId),
									new NameValuePair("tMoney",updTMoney.toString())
							};
							String updResult = HttpClientUtils.getHttpsByUrl(url_u+"updTMoney.do",paramUrl2);
						}
					}else{
						log.info("[AlipayNotifyController.AlipayNotify status]:status = "+status+" ^Enterprise account application!");
					}
				}else{
					log.info("[AlipayNotifyController.AlipayNotify trade_status]:trade_status = "+tradeStatus+" ^Enterprise account application!");
				}
				JsonUtil.outPutJson(response,"success");
			}else {//验证失败
				JsonUtil.outPutJson(response,"fail");
				log.info("[AlipayNotifyController.AlipayNotify signVerified fail]:signVerified = "+signVerified+" ^Enterprise account application!");
			}
			log.info("[AlipayNotifyController.AlipayNotify End]:uuid = "+uuid+" ^Enterprise account application!");
		} catch (Exception e) {
			log.error("[AlipayNotifyController.AlipayNotify Error]:\n",e);
		}
	}
}
