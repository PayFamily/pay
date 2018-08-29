package com.cyberbay.frog.pay.alipay.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.cyberbay.frog.pay.common.util.JsonUtil;
import com.cyberbay.frog.pay.common.util.WebConstants;
import com.cyberbay.frog.pay.common.web.controller.BaseController;

@Controller
public class AlipayController extends BaseController {
	@Value("${alipay.app.id}")
	private String ALIPAY_APP_ID;
	@Value("${alipay.getway.url}")
	private String ALIPAY_GETWAY_URL;
	@Value("${alipay.merchant.private.key}")
	private String ALIPAY_MERCHANT_PRIVATE_KEY;
	@Value("${alipay.charset}")
	private String ALIPAY_CHARSET;
	@Value("${alipay.public.key}")
	private String ALIPAY_PUBLIC_KEY;
	@Value("${alipay.sign.type}")
	private String ALIPAY_SIGN_TYPE;
	@Value("${alipay.return.url}")
	private String ALIPAY_RETURN_URL;
	@Value("${alipay.notify.url}")
	private String ALIPAY_NOTIFY_URL;
	@Value("${format}")
	private String ALIPAY_FORMAT;

	@RequestMapping(value = "/alipay", method = RequestMethod.POST)
	@ResponseBody
	public void alipay(HttpServletRequest request, HttpServletResponse response,
			// 商户订单号，商户网站订单系统中唯一订单号，必填
			@RequestParam("tradeNo") String tradeNo,
			// 付款金额，必填
			@RequestParam("totalAmount") String totalAmount,
			// 商品名称，必填
			@RequestParam("subject") String subject) {
		String uuid = UUID.randomUUID().toString();
		log.info("[alipayController.alipay Start]:uuid = " + uuid);
		log.info("[alipayController.alipay tradeNo=]"+tradeNo);
		log.info("[alipayController.alipay totalAmount=]"+totalAmount);
		log.info("[alipayController.alipay subject=]"+subject);
		Map<String, String> mapResult = new HashMap<String, String>();
		// 商品描述，可空
		String body = request.getParameter("body");
		log.info("[alipayController.alipay body=]"+body);
		try {
			// 获得初始化的AlipayClient
			AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_GETWAY_URL, ALIPAY_APP_ID,
					ALIPAY_MERCHANT_PRIVATE_KEY, ALIPAY_FORMAT, ALIPAY_CHARSET, ALIPAY_PUBLIC_KEY, ALIPAY_SIGN_TYPE);

			// 设置请求参数
			AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
			alipayRequest.setReturnUrl(ALIPAY_RETURN_URL);
			alipayRequest.setNotifyUrl(ALIPAY_NOTIFY_URL);
			alipayRequest.setBizContent("{\"out_trade_no\":\"" + tradeNo + "\"," + "\"total_amount\":\"" + totalAmount
					+ "\"," + "\"subject\":\"" + subject + "\"," + "\"body\":\"" + body + "\","
					+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

			AlipayTradePagePayResponse tradeResponse = alipayClient.pageExecute(alipayRequest);
			String result = alipayClient.pageExecute(alipayRequest).getBody();
			if (tradeResponse.isSuccess()) {
				log.info("调用成功");
				// 请求

				mapResult.put(STATUS, WebConstants.STATUS_SUCCESS);
				mapResult.put(MSG, WebConstants.MSG_SUCCESS);
				// JsonUtil.outPutJson(response,JsonUtil.toJSONString(mapResult));
			} else {
				log.error("调用失败");
				mapResult.put(STATUS, WebConstants.STATUS_ALIPAY_FUILURE);
				mapResult.put(MSG, WebConstants.MSG_ALIPAY_RESPONSE_FUILURE);
			}
			mapResult.put(DATA, result);
			log.info("[alipayController.alipay result=]"+result);
			log.info("[alipayController.alipay End]");
			JsonUtil.outPutJson(response, mapResult);

		} catch (Exception e) {
			mapResult.put(STATUS, WebConstants.STATUS_FUILURE);
			mapResult.put(MSG, WebConstants.MSG_FUILURE);
			e.printStackTrace();
			JsonUtil.outPutJson(response, JsonUtil.toJSONString(mapResult));
			log.error("[alipayController.alipay Error]:\n", e);
		}
	}

	@RequestMapping(value = "/alipayQuery", method = RequestMethod.POST)
	@ResponseBody
	public void alipayQuery(HttpServletRequest request, HttpServletResponse response,
			// 商户订单号，商户网站订单系统中唯一订单号，必填
			@RequestParam("outTradeNo") String outTradeNo) {
		String uuid = UUID.randomUUID().toString();
		// 支付宝交易号
		String tradeNo = request.getParameter("tradeNo");
		log.info("[alipayController.alipayQuery Start]:uuid = " + uuid);
		log.info("[alipayController.alipayQuery outTradeNo=]"+outTradeNo);
		Map<String, String> mapResult = new HashMap<String, String>();
		try {
			tradeNo = (tradeNo == null) ? "" : tradeNo;
			log.info("[alipayController.alipayQuery tradeNo=]"+tradeNo);
			// 获得初始化的AlipayClient
			AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_GETWAY_URL, ALIPAY_APP_ID,
					ALIPAY_MERCHANT_PRIVATE_KEY, ALIPAY_FORMAT, ALIPAY_CHARSET, ALIPAY_PUBLIC_KEY, ALIPAY_SIGN_TYPE);

			// 设置请求参数
			AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();

			alipayRequest
					.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\"," + "\"trade_no\":\"" + tradeNo + "\"}");

			AlipayTradeQueryResponse tradeResponse = alipayClient.execute(alipayRequest);
			String result = alipayClient.execute(alipayRequest).getBody();
			log.info("[alipayController.alipayQuery result=]"+result);
			if (tradeResponse.isSuccess()) {
				log.info("调用成功");
				mapResult.put(STATUS, WebConstants.STATUS_SUCCESS);
				mapResult.put(MSG, WebConstants.MSG_SUCCESS);

			} else {
				log.error("调用失败");
				mapResult.put(STATUS, WebConstants.STATUS_ALIPAY_FUILURE);
				mapResult.put(MSG, WebConstants.MSG_ALIPAY_RESPONSE_FUILURE);
			}
			mapResult.put(DATA, result);
			log.info("[alipayController.alipayQuery End]:uuid = " + uuid + " result:" + result);
			JsonUtil.outPutJson(response, mapResult);
		} catch (Exception e) {
			mapResult.put(STATUS, WebConstants.STATUS_FUILURE);
			mapResult.put(MSG, WebConstants.MSG_FUILURE);
			e.printStackTrace();
			log.error("[alipayController.alipayQuery Error]:\n", e);
			JsonUtil.outPutJson(response, mapResult);
		}
	}

	@RequestMapping(value = "/refund", method = RequestMethod.POST)
	@ResponseBody
	public void refund(HttpServletRequest request, HttpServletResponse response,
			// 需要退款的金额，该金额不能大于订单金额，必填
			@RequestParam("refundAmount") String refundAmount,
			// 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
			@RequestParam("outRequestNo") String outRequestNo

	) {
		String uuid = UUID.randomUUID().toString();
		log.info("[alipayController.refund  Start]:uuid=" + uuid);
		log.info("[alipayController.refund  outRequestNo=]"+outRequestNo);
		log.info("[alipayController.refund  refundAmount=]"+refundAmount);
		// 获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_GETWAY_URL, ALIPAY_APP_ID,
				ALIPAY_MERCHANT_PRIVATE_KEY, ALIPAY_FORMAT, ALIPAY_CHARSET, ALIPAY_PUBLIC_KEY, ALIPAY_SIGN_TYPE);
		Map<String, String> mapResult = new HashMap<String, String>();
		try {
			// 设置请求参数
			AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();

			// 商户订单号，商户网站订单系统中唯一订单号
			String outTradeNo = request.getParameter("outTradeNo");
			outTradeNo = (outTradeNo == null) ? "" : outTradeNo;
			log.info("[alipayController.refund  outTradeNo=]"+outTradeNo);
		
			// 支付宝交易号
			String tradeNo = request.getParameter("tradeNo");
			tradeNo = (tradeNo == null) ? "" : tradeNo;
			log.info("[alipayController.refund  tradeNo=]"+tradeNo);
			// 请二选一设置
			if (StringUtils.isBlank(outTradeNo) && StringUtils.isBlank(tradeNo)) {
				mapResult.put(STATUS, WebConstants.STATUS_ALIPAY_FUILURE);
				mapResult.put(MSG, WebConstants.MSG_ALIPAY_REFUND_NOT_NULL);
			} else {
				// 退款的原因说明
				String refundReason = String.valueOf(request.getParameter("refundReason"));
				alipayRequest.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\"," + "\"trade_no\":\"" + tradeNo
						+ "\"," + "\"refund_amount\":\"" + refundAmount + "\"," + "\"refund_reason\":\"" + refundReason
						+ "\"," + "\"out_request_no\":\"" + outRequestNo + "\"}");
				AlipayTradeRefundResponse refundResponse = alipayClient.execute(alipayRequest);
				String result = alipayClient.execute(alipayRequest).getBody();
				log.info("[alipayController.refund  result=]"+result);
				if (refundResponse.isSuccess()) {
					log.info("调用成功");
					mapResult.put(STATUS, WebConstants.STATUS_SUCCESS);
					mapResult.put(MSG, WebConstants.MSG_SUCCESS);
				} else {
					log.error("调用失败");
					mapResult.put(STATUS, WebConstants.STATUS_ALIPAY_FUILURE);
					mapResult.put(MSG, WebConstants.MSG_ALIPAY_RESPONSE_FUILURE);

				}
				mapResult.put(DATA, result);
			}
			JsonUtil.outPutJson(response, mapResult);
			log.info("[alipayController.refund  End]:uuid=" + uuid);
		} catch (Exception e) {
			mapResult.put(STATUS, WebConstants.STATUS_FUILURE);
			mapResult.put(MSG, WebConstants.MSG_FUILURE);
			e.printStackTrace();
			log.error("[alipayController.refund Error]:\n", e);
			JsonUtil.outPutJson(response, mapResult);
		}

	}

	@RequestMapping(value = "/refundQuery", method = RequestMethod.POST)
	@ResponseBody
	public void refundQuery(HttpServletRequest request, HttpServletResponse response,
			// 请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号，必填
			@RequestParam("outRequestNo") String outRequestNo) {
		
		String uuid = UUID.randomUUID().toString();
		log.info("[alipayController.refundQuery  Start]:uuid=" + uuid);
		log.info("[alipayController.refundQuery  outRequestNo=]"+outRequestNo);
		// 获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_GETWAY_URL, ALIPAY_APP_ID,
				ALIPAY_MERCHANT_PRIVATE_KEY, ALIPAY_FORMAT, ALIPAY_CHARSET, ALIPAY_PUBLIC_KEY, ALIPAY_SIGN_TYPE);
		Map<String, String> mapResult = new HashMap<String, String>();
		try {
			// 设置请求参数
			AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();

			// 商户订单号，商户网站订单系统中唯一订单号
			String outTradeNo = request.getParameter("outTradeNo");
			outTradeNo = (outTradeNo == null) ? "" : outTradeNo;
			log.info("[alipayController.refundQuery  outTradeNo=]"+outTradeNo);
			// 支付宝交易号
			String tradeNo = request.getParameter("tradeNo");
			tradeNo = (tradeNo == null) ? "" : tradeNo;
			log.info("[alipayController.refundQuery  tradeNo=]"+tradeNo);
			// 请二选一设置
			if (StringUtils.isBlank(outTradeNo) && StringUtils.isBlank(tradeNo)) {
				mapResult.put(STATUS, WebConstants.STATUS_ALIPAY_FUILURE);
				mapResult.put(MSG, WebConstants.MSG_ALIPAY_REFUND_NOT_NULL);
			} else {
				alipayRequest.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\"," + "\"trade_no\":\"" + tradeNo
						+ "\"," + "\"out_request_no\":\"" + outRequestNo + "\"}");
				AlipayTradeFastpayRefundQueryResponse tradeResponse = alipayClient.execute(alipayRequest);
				String result = alipayClient.execute(alipayRequest).getBody();
				log.info("[alipayController.refundQuery  result=]"+result);
				if (tradeResponse.isSuccess()) {
					log.info("调用成功");
					mapResult.put(STATUS, WebConstants.STATUS_SUCCESS);
					mapResult.put(MSG, WebConstants.MSG_SUCCESS);
				} else {
					log.error("调用失败");
					mapResult.put(STATUS, WebConstants.STATUS_ALIPAY_FUILURE);
					mapResult.put(MSG, WebConstants.MSG_ALIPAY_RESPONSE_FUILURE);

				}
				mapResult.put(DATA, result);
			}
			JsonUtil.outPutJson(response, mapResult);
			log.info("[alipayController.refundQuery  End]:uuid=" + uuid);

		} catch (Exception e) {
			mapResult.put(STATUS, WebConstants.STATUS_FUILURE);
			mapResult.put(MSG, WebConstants.MSG_FUILURE);
			e.printStackTrace();
			log.error("[alipayController.refundQuery Error]:\n", e);
			JsonUtil.outPutJson(response, mapResult);
		}

	}
	@RequestMapping(value = "/close", method = RequestMethod.POST)
	@ResponseBody
	public void close(HttpServletRequest request, HttpServletResponse response) {
		String uuid = UUID.randomUUID().toString();
		log.info("[alipayController.close  Start]:uuid=" + uuid);
		// 获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_GETWAY_URL, ALIPAY_APP_ID,
				ALIPAY_MERCHANT_PRIVATE_KEY, ALIPAY_FORMAT, ALIPAY_CHARSET, ALIPAY_PUBLIC_KEY, ALIPAY_SIGN_TYPE);
		Map<String, String> mapResult = new HashMap<String, String>();
		try {
			//设置请求参数
			AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
			// 商户订单号，商户网站订单系统中唯一订单号
			String outTradeNo = request.getParameter("outTradeNo");
			outTradeNo = (outTradeNo == null) ? "" : outTradeNo;
			log.info("[alipayController.close  outTradeNo=]"+outTradeNo);
			// 支付宝交易号
			String tradeNo = request.getParameter("tradeNo");
			tradeNo = (tradeNo == null) ? "" : tradeNo;
			log.info("[alipayController.close  tradeNo=]"+tradeNo);
			// 请二选一设置
			if (StringUtils.isBlank(outTradeNo) && StringUtils.isBlank(tradeNo)) {
				mapResult.put(STATUS, WebConstants.STATUS_ALIPAY_FUILURE);
				mapResult.put(MSG, WebConstants.MSG_ALIPAY_REFUND_NOT_NULL);
			} else {
				alipayRequest.setBizContent("{\"out_trade_no\":\""+ outTradeNo +"\"," +"\"trade_no\":\""+ tradeNo +"\"}");
				AlipayTradeCloseResponse tradeResponse = alipayClient.execute(alipayRequest);
				String result = alipayClient.execute(alipayRequest).getBody();
				log.info("[alipayController.close  result=]"+result);
				if (tradeResponse.isSuccess()) {
					log.info("调用成功");
					mapResult.put(STATUS, WebConstants.STATUS_SUCCESS);
					mapResult.put(MSG, WebConstants.MSG_SUCCESS);
				} else {
					log.error("调用失败");
					mapResult.put(STATUS, WebConstants.STATUS_ALIPAY_FUILURE);
					mapResult.put(MSG, WebConstants.MSG_ALIPAY_RESPONSE_FUILURE);
					
				}
				mapResult.put(DATA, result);
			}
			JsonUtil.outPutJson(response, mapResult);
			log.info("[alipayController.close  End]:uuid=" + uuid);
			
		} catch (Exception e) {
			mapResult.put(STATUS, WebConstants.STATUS_FUILURE);
			mapResult.put(MSG, WebConstants.MSG_FUILURE);
			e.printStackTrace();
			log.error("[alipayController.close Error]:\n", e);
			JsonUtil.outPutJson(response, mapResult);
		}
		
	}
}
