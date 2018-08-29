package com.cyberbay.frog.pay.common.util;

public class WebConstants {

	public static final String STATUS_SUCCESS = "0000";
	public static final String STATUS_FUILURE = "0001";
	public static final String STATUS_TWO_SUCCESS = "0002";
	public static final String STATUS_THREE_FUILURE = "0003";
	public static final String STATUS_USER_NOT_ACTIVE = "0004";
	public static final String STATUS_OTHER_ERROR = "9999";
	public static final String STATUS_TOKKEN_NOT_EXITED = "0006";
	public static final String MSG_TOKKEN_NOT_EXITED = "TOKKEN信息不存在！";
	public static final String STATUS_TOKKEN_EXPIRYED = "0007";
	public static final String MSG_TOKKEN_EXPIRYED = "TOKKEN信息已经过期！";
	
	public static final String STATUS_VERIFICATION_ERROR = "0008";
	public static final String MSG_VERIFICATION_ERROR = "验证码不正确！";
	
	public static final String STATUS_USER_LOCKED_ERROR = "0009";
	public static final String MSG_USER_LOCKED_ERROR = "需要输入验证码或验证码输入错误！";
	
	public static final String MSG_USER_NOT_ACTIVE = "这个用户还没有邮箱激活！";
	
	public static final String USER_OK_STAUTS = "0";
	public static final String USER_NOT_ACTIVE_STAUTS = "1";
	
	public static final String STATUS_FOUR_FUILURE = "0004";
	public static final String STATUS_FIVE_FUILURE = "0005";
	public static final String STATUS_SIX_FUILURE = "0006";

	public static final int TOKKEN_EXPIRYED_DIFF = 120;
	public static final int TOKKEN_EXPIRYED_ONE_MONTH_DIFF = 30 * 24 * 60;
	public static final String TOKKEN_DATE_FORMART = "yyyyMMddHHmmssSSS";

	public static final String PARAMETER = "参数输入异常!";

	public static final String MSG_SUCCESS = "成功,系统处理正常!";
	public static final String MSG_FUILURE = "失败,系统处理异常!";

	public static final String QUERY_SUCCESS = "查询成功,系统处理正常!";
	public static final String QUERY_SUCCESS_NO_DATA = "查询成功,未查询到数据!";
	
	public static final String SAVE_SUCCESS = "保存成功,系统查理正常!";
	public static final String SAVE_FUILURE = "保存失败,系统处理异常!";

	public static final String UPDATE_SUCCESS = "更新成功,系统处理正常!";
	public static final String UPDATE_FUILURE = "更新失败,系统处理异常!";

	public static final String DELETE_SUCCESS = "删除成功,系统处理正常!";
	public static final String DELETE_FUILURE = "删除失败,系统处理异常!";

	public static final String COMPANY_FUILURE = "企业非合作状态或企业已删除";
	public static final String UPDATE_COMPANY_INFO_FUILURE = "企业资料修改失败!";
	public static final String UPDATE_COMPANY_LOGO_FUILURE = "企业LogoUrl修改失败!";

	public static final String USER_NAME_PASSWORD_ERROR = "用户名和密码错误!";
	public static final String PASSWORD_ERROR="密码错误！";
	
	public static final String EMAIL_ERROR = "邮箱格式不正确!";

	public static final String NO_EMAIL_CODE = "未找到相应的企业!";

	public static final String UPDATE_PASSWOED_SECCESS = "密码修改成功!";
	public static final String UPDATE_PASSWOED_FUILURE = "密码修改失败!";
	public static final String UPDATE_PASSWOED_ERROR = "原密码错误!";

	public static final String SEND_PASSWOED_SECCESS = "发送邮件成功!";
	public static final String SEND_PASSWOED_FUILURE = "发送邮件失败!";

	public static final String APPLY_SUCCESS = "申请成功,系统处理正常!";
	public static final String APPLY_FUILURE = "企业管理账户已申请，不能重复申请，请耐心等待审核通知!";

	public static final String SUPPLIER_EXIST = "供应商已存在!";
	public static final String SUPPLIER_APPLY = "供应商申请，正在审核中请耐心等待!";

	public static final String EMAIL_EXIST = "邮箱已存在!";
	public static final String EMAIL_EXIST_POST = "邮箱后缀已存在!";
	public static final String PON_EXIST = "电话已存在!";

	public static final String BANK_EXIST = "银行卡已存在";

	public static final String NO_USER = "用户不存在!";
	public static final String NO_TIME = "账号已过期!";

	public static final String MSG_GETCASH_SUCCESS = "成功，用户提取现金成功";
	public static final String MSG_GETCASH_FUILUER = "失败，用户提取现金失败!";
 
	public static final String PON_EMAIL_EXIST = "用户已存在!";
	public static final String COM_EMAIL_EXIST = "企业名称已存在!";
	public static final String COM_EXIST = "企业已存在!";

	public static final String LINK_FUILURE = "链接无效!";

	public static final String Coupons_FULLURE = "该优惠券已兑换，不能重复兑换";
	public static final String Coupons_BINDING_SUCCESS = "优惠券绑定成功";
	public static final String Coupons_BINDING_FULLURE = "优惠券绑定失败";
	public static final String Couponse_OVERDUE = "该优惠券已过期不能兑换";

	/*-------------HR---------------------*/
	public static final String BRANG_AND_BUSSHIELD_SUCCESS = "品牌及商家屏蔽成功!";
	public static final String BRANG_AND_BUSSHIELD_FUILURE = "品牌及商家屏蔽失败!";

	public static final String SHIELD_SUCCESS = "屏蔽成功!";
	public static final String SHIELD_CANCEL_SUCCESS = "取消屏蔽成功!";

	public static final String AUDIT_STATUS = "00";
	public static final String USER_LEVE = "用户等级";

	public static final String SEND_SUCCESS = "发送成功";
	public static final String SEND_FUILURE_PARAM_MOBILENO_BLANK = "手机号为空。";
	public static final String SEND_FUILURE_PARAM_CONTENT_BLANK = "发送内容为空。";
	
	public static final String PAY_SUCCESS = "支付成功";
	public static final String PAY_FUILURE_TMONEY_BLANK = "支付失败福利币不足";
	public static final String PAY_FUILURE = "支付失败";
	
	public static final String STATUS_ALIPAY_FUILURE="0011";
	
	
	public static final String MSG_ALIPAY_RESPONSE_FUILURE="支付宝请求失败！";
	
	public static final String MSG_ALIPAY_REFUND_NOT_NULL="商户订单号和支付宝交易号不能同时为空！";
	
}
