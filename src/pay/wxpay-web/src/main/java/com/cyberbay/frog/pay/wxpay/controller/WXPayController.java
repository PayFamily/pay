package com.cyberbay.frog.pay.wxpay.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.cyberbay.frog.pay.common.util.HttpClientUtils;
import com.cyberbay.frog.pay.common.util.JsonUtil;
import com.cyberbay.frog.pay.common.util.WebConstants;
import com.cyberbay.frog.pay.common.web.controller.BaseController;
import com.cyberbay.frog.pay.wxpay.wx.sdk.WXPay;
import com.cyberbay.frog.pay.wxpay.wx.sdk.WXPayConstants.SignType;
import com.cyberbay.frog.pay.wxpay.wx.sdk.WXPayUtil;
import com.cyberbay.frog.pay.wxpay.wx.sdk.impl.WXPayConfigImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
//@RequestMapping("/bsys")
public class WXPayController extends BaseController {
	// 订单状态 01 为未支付
	private static final String ORDER_STATUS01 = "01";
	// 系统处理成功code码
	private static final String SUCCESS_CODE = "0000";
	// 订单状态 02 为待发货
	private static final String ORDER_STATUS02 = "02";

	@Value("${weixin.app.id}")
	private String WEIXIN_APP_ID;
	@Value("${weixin.app.key}")
	private String WEIXIN_APP_KEY;
	@Value("${weixin.api.key}")
	private String WEIXIN_API_KEY;
	@Value("${weixin.oauth.url}")
	private String WEIXIN_OAUTH_URL;
	@Value("${weixin.api.request.url}")
	private String WEIXIN_API_REQUEST_URL;
	@Value("${weixin.merchant.id}")
	private String WEIXIN_MERCHANT_ID;
	@Value("${weixin.notify.url}")
	private String WEIXIN_NOTIFY_URL;
	@Value("${weixin.sp.bill.create.ip}")
	private String WEIXIN_SP_BILL_CREATE_IP;
	// 支付完成跳转页面路径
	@Value("${weixin.redirect.url}")
	private String WEIXIN_REDIRECT_URL;
	@Value("${weixin.app.secret}")
	private String WEIXIN_APP_SECRET;
	public static String prepayId = "prepay_id";
	public static String sign = "sign";

	// 微信公众号支付使用
	// 取得支付ID和签名
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/getPrepayIdAndSign", method = RequestMethod.GET)
	@ResponseBody
	public void getPrepayId(HttpServletRequest request, HttpServletResponse response,
			// 微信用户Wcode
			@RequestParam("openId") String openId, @RequestParam("body") String body,
			@RequestParam("outTradeNo") String outTradeNo, @RequestParam("totalFee") String totalFee) {
		// public static void getPrepayId(String openId) {
		Map map = new HashMap();
		log.info("getPrepayId START ");
		try {

			String attach = "applytest";
			// String body = "JSAPIapplytest";
			// body =
			// "\u5317\u4eac\u4e07\u5f80\u79d1\u6280\u6709\u9650\u516c\u53f8\u5546\u54c1";
			this.log.info("body:" + body);
			// String Ubody = getStrUnicode(body);
			// this.log.info("Ubody:"+Ubody);
			String nonceStr = "1add1a30ac87aa2db72f57a2375d8fec";
			// String totalFee = "1";
			String tradeType = "JSAPI";

			String xmlStr = unifiedOrder(openId, attach, body, WEIXIN_MERCHANT_ID, nonceStr, WEIXIN_NOTIFY_URL,
					outTradeNo, WEIXIN_SP_BILL_CREATE_IP, totalFee, tradeType);
			String prepayIdStr = getXmlParam(xmlStr, prepayId);
			this.log.info("******************" + prepayIdStr + "*****************");
			String signStr = getXmlParam(xmlStr, sign);
			String timeStamp = String.valueOf(new Date().getTime());
			timeStamp = timeStamp.substring(0, timeStamp.length() - 3);
			String paySign = getPaySign(timeStamp, nonceStr, prepayIdStr);

			Map mapData = new HashMap();
			mapData.put("appId", WEIXIN_APP_ID);
			mapData.put("prepayId", prepayIdStr);
			mapData.put("paySign", paySign);
			mapData.put("signType", "MD5");
			mapData.put("timeStamp", timeStamp);
			mapData.put("nonceStr", nonceStr);
			map.put("data", mapData);
			map.put("status", WebConstants.STATUS_SUCCESS);
			map.put("msg", WebConstants.MSG_SUCCESS);
			log.info("getPrepayId END " + prepayIdStr);
			JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
		} catch (Exception e) {
			// TODO: handle exception
			map.put("status", WebConstants.STATUS_FUILURE);
			map.put("msg", WebConstants.MSG_FUILURE);
			JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
			log.error("[wxpayController.getOpenId Error]:\n", e);
		}
	}

	// 取得openID
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/getOpenId", method = RequestMethod.GET)
	@ResponseBody
	public void getOpenId(HttpServletRequest request, HttpServletResponse response,
			// 微信用户Wcode
			@RequestParam("code") String code) {
		Map map = new HashMap();
		log.info("getOpenId start code:" + code);
		String openId = null;
		try {
			openId = getOpenId(code);
			log.info("openId:" + openId);
			map.put("data", openId);
			map.put("status", WebConstants.STATUS_SUCCESS);
			map.put("msg", WebConstants.MSG_SUCCESS);
			JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
		} catch (Exception e) {
			map.put("status", WebConstants.STATUS_FUILURE);
			map.put("msg", WebConstants.MSG_FUILURE);
			JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
			log.error("[wxpayController.getOpenId Error]:\n", e);
		}
	}
	/*
	 * // 查询订单
	 * 
	 * @SuppressWarnings({ "rawtypes", "unchecked" })
	 * 
	 * @RequestMapping(value = "/orderquery", method = RequestMethod.GET)
	 * 
	 * @ResponseBody public void orderquery(HttpServletRequest request,
	 * HttpServletResponse response, // 商户订单号
	 * 
	 * @RequestParam("outTradeNo") String outTradeNo) { Map map = new HashMap();
	 * log.info("orderquery START "); try {
	 * 
	 * String nonceStr = "1add1a30ac87aa2db72f57a2375d8fec"; String orderqueryUrl =
	 * WEIXIN_API_REQUEST_URL + "orderquery";
	 * 
	 * OrderqueryRequest orderqueryRequest = new OrderqueryRequest();
	 * orderqueryRequest.setAppid(WEIXIN_APP_ID);
	 * orderqueryRequest.setMch_id(WEIXIN_MERCHANT_ID);
	 * orderqueryRequest.setNonce_str(nonceStr);
	 * orderqueryRequest.setOut_trade_no(outTradeNo); String signXmlStr =
	 * JAXBUtil.convertToXml(orderqueryRequest); String sign =
	 * WeiXinPayUtils.createSign(WeiXinPayUtils.doXMLParse(signXmlStr),
	 * WEIXIN_API_KEY); orderqueryRequest.setSign(sign); String requestXmlStr =
	 * JAXBUtil.convertToXml(orderqueryRequest); String responseBody =
	 * HttpClientUtils.sendXMLDataByPost(orderqueryUrl,requestXmlStr); Map mapData =
	 * new HashMap();
	 * 
	 * OrderqueryResponse orderqueryResponse
	 * =JAXBUtil.converyToJavaBean(responseBody, OrderqueryResponse.class);
	 * 
	 * mapData.put("responseBody", orderqueryResponse); map.put("data", mapData);
	 * map.put("status", WebConstants.STATUS_SUCCESS); map.put("msg",
	 * WebConstants.MSG_SUCCESS); log.info("orderquery END ");
	 * JsonUtil.outPutJson(response, JsonUtil.toJSONString(map)); } catch (Exception
	 * e) { // TODO: handle exception map.put("status",
	 * WebConstants.STATUS_FUILURE); map.put("msg", WebConstants.MSG_FUILURE);
	 * JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
	 * log.error("[wxpayController.orderquery Error]:\n", e); } }
	 */

	// 根据code发送http请求在微信下单
	public String unifiedOrder(String openId, String attach, String body, String mchId, String nonceStr,
			String notifyUrl, String outTradeNo, String spbillCreateIp, String totalFee, String tradeType) {
		try {
			log.info("unifiedOrder START ");
			// 获取openId
			String unifiedorderUrl = WEIXIN_API_REQUEST_URL + "unifiedorder";
			// String openId =getOpenId(code);
			URL url = new URL(unifiedorderUrl);
			URLConnection con = url.openConnection();
			con.setDoOutput(true);
			con.setRequestProperty("Pragma:", "no-cache");
			con.setRequestProperty("Cache-Control", "no-cache");
			con.setRequestProperty("Content-Type", "text/xml");

			OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());

			String xmlInfo = getXmlInfo(openId, attach, body, mchId, nonceStr, notifyUrl, outTradeNo, spbillCreateIp,
					totalFee, tradeType);
			log.info("xmlInfo := " + xmlInfo);
			System.out.println("urlStr=" + unifiedorderUrl);
			System.out.println("xmlInfo=" + xmlInfo);
			// out.write(new String(xmlInfo.getBytes("ISO-8859-1"),"utf-8"));
			out.write(xmlInfo);
			out.flush();
			out.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = "";
			String xmlStr = "";
			for (line = br.readLine(); line != null; line = br.readLine()) {
				System.out.println(line);
				xmlStr += line;
			}
			log.info("unifiedOrder END " + xmlStr);
			return xmlStr;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 根据code发送http请求获得openId
	private String getOpenId(String code) {
		String openId = null;
		String result = "";
		log.info("private getOpnid  START");
		try {
			String requestUrl = WEIXIN_OAUTH_URL + "access_token";
			NameValuePair[] param = { null, null, null, null };
			param[0] = new NameValuePair("appid", WEIXIN_APP_ID);
			param[1] = new NameValuePair("secret", WEIXIN_APP_SECRET);
			param[2] = new NameValuePair("code", code);
			param[3] = new NameValuePair("grant_type", "authorization_code");
			result = HttpClientUtils.postHttpsByUrl(requestUrl, param);
			log.info("private getOpnid  END------result=" + result);
			JSONObject json = JSONObject.fromObject(result);
			openId = json.getString("openid");
			log.info("private getOpnid  END------opendId=" + openId);
		} catch (Exception e) {
			log.error("[getOpenId Error]:\n get Result:= " + result, e);
			e.printStackTrace();
			return null;
		}
		return openId;
	}

	// 根据openId拼接发送http请求的<xml>参数
	private String getPaySign(String timeStamp, String nonceStr, String prepayId) {
		String sourceSignWord = "WEIXIN_APP_ID=" + WEIXIN_APP_ID;
		sourceSignWord = sourceSignWord + "&nonceStr=" + nonceStr;
		sourceSignWord = sourceSignWord + "&package=prepay_id=" + prepayId;
		sourceSignWord = sourceSignWord + "&signType=MD5";
		sourceSignWord = sourceSignWord + "&timeStamp=" + timeStamp;
		sourceSignWord = sourceSignWord + "&key=" + WEIXIN_API_KEY;
		// String md5Str = Md5Security.getMD5(sourceSignWord).toUpperCase();
		/*String md5Str = MD5Util.MD5Encode(sourceSignWord, "UTF-8").toUpperCase();
		this.log.info(sourceSignWord);
		System.out.println(md5Str + "---" + sourceSignWord);
		return md5Str;*/
		return "";
	}

	// 根据openId拼接发送http请求的<xml>参数
	private String getXmlInfo(String openId, String attach, String body, String mchId, String nonceStr,
			String notifyUrl, String outTradeNo, String spbillCreateIp, String totalFee, String tradeType) {
		String sign = signWord(openId, attach, body, mchId, nonceStr, notifyUrl, outTradeNo, spbillCreateIp, totalFee,
				tradeType);
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		sb.append("    <appid>" + WEIXIN_APP_ID + "</appid>");
		sb.append("    <attach>" + attach + "</attach>");
		sb.append("    <body>" + body + "</body>");
		sb.append("    <mch_id>" + mchId + "</mch_id>");
		sb.append("    <nonce_str>" + nonceStr + "</nonce_str>");
		sb.append("    <notify_url>" + notifyUrl + "</notify_url>");
		sb.append("    <openid>" + openId + "</openid>");
		sb.append("    <out_trade_no>" + outTradeNo + "</out_trade_no>");
		sb.append("    <spbill_create_ip>" + spbillCreateIp + "</spbill_create_ip>");
		sb.append("    <total_fee>" + totalFee + "</total_fee>");
		sb.append("    <trade_type>" + tradeType + "</trade_type>");
		sb.append("    <sign>" + sign + "</sign>");
		sb.append("</xml>");
		return sb.toString();
	}

	// 根据openId活动加密签名sign
	private String signWord(String openId, String attach, String body, String mchId, String nonceStr, String notifyUrl,
			String outTradeNo, String spbillCreateIp, String totalFee, String tradeType) {
		String sourceSignWord = "appid=" + WEIXIN_APP_ID;
		sourceSignWord = sourceSignWord + "&attach=" + attach;
		sourceSignWord = sourceSignWord + "&body=" + body;
		sourceSignWord = sourceSignWord + "&mch_id=" + mchId;
		sourceSignWord = sourceSignWord + "&nonce_str=" + nonceStr;
		sourceSignWord = sourceSignWord + "&notify_url=" + notifyUrl;
		sourceSignWord = sourceSignWord + "&openid=" + openId;
		sourceSignWord = sourceSignWord + "&out_trade_no=" + outTradeNo;
		sourceSignWord = sourceSignWord + "&spbill_create_ip=" + spbillCreateIp;
		sourceSignWord = sourceSignWord + "&total_fee=" + totalFee;
		sourceSignWord = sourceSignWord + "&trade_type=" + tradeType;
		sourceSignWord = sourceSignWord + "&key=" + WEIXIN_API_KEY;

		/*String md5Str = MD5Util.MD5Encode(sourceSignWord, "UTF-8").toUpperCase();
		return md5Str;*/
		// return Md5Security.getMD5(sourceSignWord).toUpperCase();
		return "";
	}

	// 解析xml(传入Key，返回Value)
	public String getXmlParam(String xmlStr, String paramKey) {
		try {
			StringReader sr = new StringReader(xmlStr);
			InputSource is = new InputSource(sr);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(is);
			NodeList nList = doc.getElementsByTagName("xml");
			// 遍历该集合，显示结合中的元素及其子元素的名字
			for (int i = 0; i < nList.getLength(); i++) {
				Element node = (Element) nList.item(i);
				System.out.println(
						paramKey + ":" + node.getElementsByTagName(paramKey).item(0).getFirstChild().getNodeValue());
				String returnStr = node.getElementsByTagName(paramKey).item(0).getFirstChild().getNodeValue();
				return returnStr;
			}
			log.info("getXmlParam END " + xmlStr);
		} catch (Exception e) {
			// TODO: handle exception
			log.error("getXmlParam ERROR ", e);
		}
		return null;
	}

	public static String getStrUnicode(String inStr) {
		StringBuffer unicode = new StringBuffer();
		char c;
		int bit;
		String tmp = null;
		for (int i = 0; i < inStr.length(); i++) {
			c = inStr.charAt(i);
			if (c > 255) {
				unicode.append("\\u");
				bit = (c >>> 8);
				tmp = Integer.toHexString(bit);
				if (tmp.length() == 1)
					unicode.append("0");
				unicode.append(tmp);
				bit = (c & 0xFF);
				tmp = Integer.toHexString(bit);
				if (tmp.length() == 1)
					unicode.append("0");
				unicode.append(tmp);
			} else {
				unicode.append(c);
			}
		}
		return (new String(unicode));
	}

	/**
	 * 微信支付
	 * 
	 * @param request
	 * @param response
	 * @param out_trade_no
	 * @param total_fee
	 * @param product_id
	 * @param body
	 */
	@RequestMapping(value = "/wxpay", method = RequestMethod.GET)
	@ResponseBody
	public void wxpay(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("outTradeNo") String outTradeNo, @RequestParam("totalFee") String totalFee,
			@RequestParam("productId") String productId, @RequestParam("body") String body

	) {
		Map<String, String> mapResult = new HashMap<String, String>();
		try {
			log.info("unifiedOrder START ");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("body", body);
			data.put("out_trade_no", outTradeNo);
			data.put("total_fee", totalFee);
			data.put("product_id", productId);
			data.put("spbill_create_ip", WEIXIN_SP_BILL_CREATE_IP);
			data.put("notify_url", WEIXIN_NOTIFY_URL);
			data.put("trade_type", "NATIVE");
			data.put("device_info", "");
			data.put("fee_type", "CNY");
			WXPay wxpay = new WXPay(WXPayConfigImpl.getInstance(makeParams()));
			Map<String, String> returnMap = wxpay.unifiedOrder(data);
			if (returnMap != null) {
				if ("SUCCESS".equals(returnMap.get("return_code")) && "SUCCESS".equals(returnMap.get("result_code"))) {
					String codeUrl = (String) returnMap.get("code_url");
					mapResult.put("codeUrl", codeUrl);
					mapResult.put("status", WebConstants.STATUS_SUCCESS);
					mapResult.put("msg", WebConstants.MSG_SUCCESS);
					JsonUtil.outPutJson(response, JsonUtil.toJSONString(mapResult));
					log.info("unifiedOrder End  outTradeNo:" + outTradeNo + " totalFee:" + totalFee);
				} else {
					mapResult.put("status", WebConstants.STATUS_THREE_FUILURE);
					mapResult.put("msg", returnMap.get("err_code_des"));
					JsonUtil.outPutJson(response, JsonUtil.toJSONString(mapResult));
					log.info("unifiedOrder End  outTradeNo:" + outTradeNo + " totalFee:" + totalFee);
				}
			} else {
				mapResult.put("status", WebConstants.STATUS_FUILURE);
				mapResult.put("msg", WebConstants.MSG_FUILURE);
				JsonUtil.outPutJson(response, JsonUtil.toJSONString(mapResult));
				log.info("unifiedOrder error outTradeNo:" + outTradeNo + " totalFee:" + totalFee);
			}

		} catch (Exception e) {
			mapResult.put("status", WebConstants.STATUS_FUILURE);
			mapResult.put("msg", WebConstants.MSG_FUILURE);
			JsonUtil.outPutJson(response, JsonUtil.toJSONString(mapResult));
			log.error("[wxpayController.unifiedOrder Error  outTradeNo:" + outTradeNo + " totalFee:" + totalFee
					+ "]:\n", e);
		}
	}

	@RequestMapping(value = "/weixinNotify", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public void weixinNotify(HttpServletRequest request, HttpServletResponse response) {
		try {
			this.log.info("[WxpayController.weixinNotify Satrt]");
			// 读取参数
			InputStream inputStream;
			StringBuffer sb = new StringBuffer();
			inputStream = request.getInputStream();
			String s;
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((s = in.readLine()) != null) {
				sb.append(s);
			}
			in.close();
			inputStream.close();

			// 解析xml成map
			log.info("----------------xml=" + sb.toString());
			Map<String, String> m = new HashMap<String, String>();
			m = WXPayUtil.xmlToMap(sb.toString());
			// 过滤空 设置 TreeMap
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			Iterator it = m.keySet().iterator();
			while (it.hasNext()) {
				String parameter = (String) it.next();
				String parameterValue = m.get(parameter);
				String v = "";
				if (null != parameterValue) {
					v = parameterValue.trim();
				}
				packageParams.put(parameter, v);
			}

			// 账号信息
			// String key = WXPayConfigImpl.getInstance().getKey(); // key
			log.info("----------------key:" + WEIXIN_API_KEY);
			log.info(packageParams);
			// 判断签名是否正确
			if (WXPayUtil.isSignatureValid(packageParams, WEIXIN_API_KEY,
					SignType.HMACSHA256)) {
				log.info("------------------sign=true");
				// ------------------------------
				// 处理业务开始
				// ------------------------------
				String resXml = "";
				if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
					// 这里是支付成功
					////////// 执行自己的业务逻辑////////////////
					String out_trade_no = (String) packageParams.get("out_trade_no");
					String total_fee = (String) packageParams.get("total_fee");
					// 查询订单状态
					NameValuePair[] paramUrl = { new NameValuePair("orderId", out_trade_no),
							new NameValuePair("delFlg", "0") };
					String orderResult = HttpClientUtils.getHttpsByUrl("" + "getOrder.do", paramUrl);
					JSONObject orderJson = JSONObject.fromObject(orderResult);
					String status = (String) orderJson.get("status");
					if (status.equals(WebConstants.STATUS_SUCCESS)) {
						JSONArray orderArray = orderJson.getJSONArray("data");
						JSONObject oderObj = orderArray.getJSONObject(0);
						// 订单状态
						String orderStatus = oderObj.get("orderStatus").toString();
						// 订单金额
						String orderAmount = oderObj.getString("orderAmount").toString();
						// 用户ID
						String userId = oderObj.getString("userId");
						// 返券数
						Integer commodityReturnTmoney = oderObj.getInt("commodityReturnTmoney");
						// 使用的福利券
						Integer tMoneyUse = oderObj.getInt("tMoneyUse");
						// 额外返券数
						Integer additionalReturnTmoney = oderObj.getInt("additionalReturnTmoney");
						// 更新后的福利券
						Integer updTMoney = 0;
						// 查询用户T币
						NameValuePair[] paramUrl1 = { new NameValuePair("userId", userId) };
						String tMoneyResult = HttpClientUtils.getHttpsByUrl("" + "searchUserTmoney.do", paramUrl1);
						this.log.info("-------tMoneyResult:" + tMoneyResult);
						JSONObject tMoneyJson = JSONObject.fromObject(tMoneyResult);
						this.log.info("-------tMoneyJson:" + tMoneyJson);
						String tMoney = tMoneyJson.get("data").toString();
						Integer tm = Integer.parseInt(tMoney);
						if (ORDER_STATUS01.equals(orderStatus)) {
							this.log.info("-------------update");
							NameValuePair[] param = { new NameValuePair("orderId", out_trade_no),
									new NameValuePair("orderStatus", ORDER_STATUS02) };
							HttpClientUtils.getHttpsByUrl("" + "updateOrder.do", param);
							updTMoney = tm - tMoneyUse + commodityReturnTmoney + additionalReturnTmoney;
							NameValuePair[] paramUrl2 = { new NameValuePair("userId", userId),
									new NameValuePair("tMoney", updTMoney.toString()) };
							String updResult = HttpClientUtils.getHttpsByUrl("" + "updTMoney.do", paramUrl2);
							this.log.info(
									" 更新福利券---result:" + updResult + " userId=" + userId + "  福利券数量更新为：" + updTMoney);
						} else {
							this.log.info("[WxpayController.weixinNotify out_trade_no:]" + out_trade_no + " total_fee"
									+ total_fee + "  status = " + status + " ^Enterprise account application!");
						}
					} else {
						this.log.info("[WxpayController.weixinNotify status]:status = " + status
								+ " ^Enterprise account application!");
					}
					// log.info("is_subscribe:"+is_subscribe);
					log.info("out_trade_no:" + out_trade_no);
					// log.info("total_fee:"+total_fee);

					////////// 执行自己的业务逻辑////////////////

					log.info("支付成功     ");
					// 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
					resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
							+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

				} else {
					log.info("支付失败,错误信息：" + packageParams.get("err_code"));
					resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
							+ "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
				}
				// ------------------------------
				// 处理业务完毕
				// ------------------------------
				BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
				out.write(resXml.getBytes());
				out.flush();
				out.close();
				// response.sendRedirect("http://qiyefuligou.com/web/pc/html/common/zhongxin.html");
			} else {
				log.info("通知签名验证失败");
			}
		} catch (Exception e) {
			log.error("[wxpayController.unifiedOrder Error]:\n", e);
		}
	}

	@RequestMapping(value = "/wxOrderQuery", method = RequestMethod.GET)
	@ResponseBody
	public void wxOrderQuery(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("outTradeNo") String outTradeNo) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			log.info("wxOrderQuery START ");
			if (StringUtils.isNotEmpty(outTradeNo)) {
				//WXPayService wxPay = new WXPayService(makeParams());
				//Map<String, String> resultMap = wxPay.doOrderQuery(outTradeNo);
				Map<String, String> resultMap=new HashMap<>();
				if (resultMap.containsKey("trade_state") && "SUCCESS".equals(resultMap.get("trade_state"))) {
					map.put("status", WebConstants.STATUS_SUCCESS);
					map.put("msg", WebConstants.MSG_SUCCESS);
					JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
					log.info("wxOrderQuery End success out_trade_no:" + outTradeNo);
				} else if (resultMap.containsKey("code")) {
					map.put("status", WebConstants.STATUS_THREE_FUILURE);
					map.put("msg", "service error");
					JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
					log.info("wxOrderQuery End service error ");
				} else {
					map.put("status", WebConstants.STATUS_TWO_SUCCESS);
					map.put("msg", WebConstants.MSG_SUCCESS);
					JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
					log.info("wxOrderQuery End success out_trade_no:" + outTradeNo);
				}

			} else {
				map.put("status", WebConstants.STATUS_THREE_FUILURE);
				map.put("msg", WebConstants.PARAMETER);
				JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
				log.info("wxOrderQuery End paramter error ");
			}

		} catch (Exception e) {
			map.put("status", WebConstants.STATUS_FUILURE);
			map.put("msg", WebConstants.MSG_FUILURE);
			JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
			log.error("[wxpayController.wxOrderQuery Error  ]:\n", e);
		}
	}

	private Map<String, String> makeParams() {
		Map<String, String> params = new HashMap<>();
		params.put("weixin.sp.bill.create.ip", WEIXIN_SP_BILL_CREATE_IP);
		params.put("weixin.notify.url", WEIXIN_NOTIFY_URL);
		params.put("weixin.app.id", WEIXIN_APP_ID);
		params.put("weixin.app.key", WEIXIN_API_KEY);
		params.put("weixin.api.key", WEIXIN_API_KEY);
		params.put("weixin.oauth.url", WEIXIN_OAUTH_URL);
		params.put("weixin.api.request.url", WEIXIN_API_REQUEST_URL);
		params.put("weixin.merchant.id", WEIXIN_MERCHANT_ID);
		return params;
	}

	@RequestMapping(value = "/weixinH5Pay", method = RequestMethod.GET)
	@ResponseBody
	public void weixinH5Pay(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("out_trade_no") String out_trade_no, @RequestParam("total_fee") String total_fee,
			@RequestParam("product_id") String product_id, @RequestParam("body") String body,
			@RequestParam("ip") String ip

	) {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest httpServletRequest = sra.getRequest();
		Map<String, String> map = new HashMap<String, String>();
		try {
			Enumeration e1 = httpServletRequest.getHeaderNames();
			while (e1.hasMoreElements()) {
				String headerName = (String) e1.nextElement();
				String headValue = request.getHeader(headerName);
				log.info("--------------------" + headerName + "=" + headValue + "------------------------------");
			}
			log.info("weixinH5Pay START ");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("body", body);
			data.put("out_trade_no", out_trade_no);
			data.put("total_fee", total_fee);
			data.put("product_id", product_id);
			data.put("spbill_create_ip", ip);
			data.put("notify_url", WEIXIN_NOTIFY_URL);
			data.put("trade_type", "MWEB");
			/*WXPayService wxPay = new WXPayService(makeParams());
			Map<String, String> returnMap = wxPay.wxUnifiedOrder(data);*/
			Map<String, String> returnMap=new HashMap<>();
			if (returnMap != null) {
				if ("SUCCESS".equals(returnMap.get("return_code")) && "SUCCESS".equals(returnMap.get("result_code"))) {
					String mwebUrl = (String) returnMap.get("mweb_url");
					mwebUrl = mwebUrl + "&redirect_url=" + WEIXIN_REDIRECT_URL;
					map.put("mwebUrl", mwebUrl);
					map.put("status", WebConstants.STATUS_SUCCESS);
					map.put("msg", WebConstants.MSG_SUCCESS);
					JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
					log.info("weixinH5Pay ip=" + ip + "  mwebUrl=" + mwebUrl);
					log.info("weixinH5Pay End  out_trade_no:" + out_trade_no + " total_fee:" + total_fee);
				} else {
					map.put("status", WebConstants.STATUS_THREE_FUILURE);
					map.put("msg", returnMap.get("err_code_des"));
					JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
					log.info("weixinH5Pay End  out_trade_no:" + out_trade_no + " total_fee:" + total_fee);
				}
			} else {
				map.put("status", WebConstants.STATUS_FUILURE);
				map.put("msg", WebConstants.MSG_FUILURE);
				JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
				log.info("weixinH5Pay error out_trade_no:" + out_trade_no + " total_fee:" + total_fee);
			}

		} catch (Exception e) {
			map.put("status", WebConstants.STATUS_FUILURE);
			map.put("msg", WebConstants.MSG_FUILURE);
			JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
			log.error("[wxpayController.weixinH5Pay Error  out_trade_no:" + out_trade_no + " total_fee:" + total_fee
					+ "]:\n", e);
		}
	}

	@RequestMapping(value = "/wxOfficialAccountsPay", method = RequestMethod.GET)
	@ResponseBody
	public void wxOfficialAccountsPay(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("out_trade_no") String out_trade_no, @RequestParam("total_fee") String total_fee,
			@RequestParam("product_id") String product_id, @RequestParam("body") String body,
			@RequestParam("ip") String ip, @RequestParam("openId") String openId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			log.info("wxOfficialAccountsPay START ");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("body", body);
			data.put("out_trade_no", out_trade_no);
			data.put("total_fee", total_fee);
			data.put("product_id", product_id);
			data.put("spbill_create_ip", ip);
			data.put("notify_url", WEIXIN_NOTIFY_URL);
			data.put("trade_type", "JSAPI");
			data.put("openid", "openId");
			data.put("device_info", "WEB");
		/*	WXPayService wxPay = new WXPayService(makeParams());
			Map<String, String> returnMap = wxPay.wxUnifiedOrder(data);*/
			Map<String, String> returnMap=new HashMap<>();
			if (returnMap != null) {
				if ("SUCCESS".equals(returnMap.get("return_code")) && "SUCCESS".equals(returnMap.get("result_code"))) {
					String timeStamp = String.valueOf(new Date().getTime());
					timeStamp = timeStamp.substring(0, timeStamp.length() - 3);
					String prepayId = (String) returnMap.get("prepay_id");
					String sign = (String) returnMap.get("sign");
					String nonceStr = (String) returnMap.get("nonce_str");
					map.put("appId", WEIXIN_APP_ID);
					map.put("timeStamp", timeStamp);
					map.put("nonceStr", nonceStr);
					map.put("package", "prepay_id=" + prepayId);
					map.put("paySign", sign);
					map.put("status", WebConstants.STATUS_SUCCESS);
					map.put("msg", WebConstants.MSG_SUCCESS);
					JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
					log.info("wxOfficialAccountsPay ip=" + ip + "  prepayId=" + prepayId);
					log.info("wxOfficialAccountsPay End  out_trade_no:" + out_trade_no + " total_fee:" + total_fee);
				} else {
					map.put("status", WebConstants.STATUS_THREE_FUILURE);
					map.put("msg", returnMap.get("err_code_des"));
					JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
					log.info("wxOfficialAccountsPay End  out_trade_no:" + out_trade_no + " total_fee:" + total_fee);
				}
			} else {
				map.put("status", WebConstants.STATUS_FUILURE);
				map.put("msg", WebConstants.MSG_FUILURE);
				JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
				log.info("wxOfficialAccountsPay error out_trade_no:" + out_trade_no + " total_fee:" + total_fee);
			}

		} catch (Exception e) {
			map.put("status", WebConstants.STATUS_FUILURE);
			JsonUtil.outPutJson(response, JsonUtil.toJSONString(map));
			log.error("[wxpayController.wxOfficialAccountsPay Error  out_trade_no:" + out_trade_no + " total_fee:"
					+ total_fee + "]:\n", e);
		}
	}
}
