package com.example.yeepay;

import com.example.yeepay.vo.CallYeePayVo;
import com.example.yeepay.yeepay.ZGTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * PaymentServiceImpl 表数据服务层接口实现类
 *
 */
@Service
public class FinPaymentServiceImpl implements FinPaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FinPaymentServiceImpl.class);

    private static final String CUSTOMER_NUMBER = "10000447996";
    private static final String KEY_FOR_HMAC = "jj3Q1h0H86FZ7CD46Z5Nr35p67L199WdkgETx85920n128vi2125T9KY2hzv";

    public static final String module = FinPaymentServiceImpl.class.getName();

    public static String formatStr(String text) {
        return text == null ? "0" : text.trim();
    }
    public static String formatStr(String[] strs) {
        return strs == null ? "" : strs[0].trim();
    }


    String requestId = "1470896215698";

    /**
     * 易宝支付接口
     * @request
     * @response
     */
    public String callYeePay(CallYeePayVo vo) {
        ZGTUtils zgtUtils = new ZGTUtils(CUSTOMER_NUMBER, KEY_FOR_HMAC);
        //支付请求号，purchase + 时间戳

        vo.setAmount("0.01");
        vo.setProductname("测试商品名");
        vo.setProductdesc("这是一条描述");

        requestId = String.valueOf(System.currentTimeMillis());

        String callBackUrl = "http://com.your.address/yeePayNotify";  //支付成功后台通知接口
        String webCallBackUrl = "http://www.baidu.com";  //支付成功跳转的网页
        String amount = "0.01";

        Map<String, String> params = new HashMap<String, String>();
        params.put("requestid", requestId);
        params.put("amount", amount);
        params.put("assure", vo.getAssure());
        params.put("productname", vo.getProductname());
        params.put("productcat", vo.getProductcat());
        params.put("productdesc", vo.getProductdesc());
        params.put("divideinfo", vo.getDivideinfo());
        params.put("callbackurl", callBackUrl);
        params.put("webcallbackurl", webCallBackUrl);
        params.put("bankid", vo.getBankid());
        params.put("period", vo.getPeriod());
        params.put("memo", vo.getMemo());
        params.put("payproducttype", vo.getPayproducttype());
        params.put("userno", vo.getUserno());
        params.put("isbind", vo.getIsbind());
        params.put("bindid", vo.getBindid());
        params.put("ip", vo.getIp());
        params.put("cardname", vo.getCardname());
        params.put("idcard", vo.getIdcard());
        params.put("bankcardnum", vo.getBankcardnum());
        params.put("mobilephone", vo.getMobilephone());
        params.put("cvv2", vo.getCvv2());
        params.put("expiredate", vo.getExpiredate());
        params.put("mcc", vo.getMcc());
        params.put("areacode", vo.getAreacode());
        params.put("ledgerno", vo.getLedgerno());

        // 第一步 生成密文data
        String data = zgtUtils.buildData(params, zgtUtils.PAYAPI_REQUEST_HMAC_ORDER);

        // 第二步 发起请求
        String requestUrl = zgtUtils.getRequestUrl(zgtUtils.PAYAPI_NAME);
        Map<String, String> responseMap = zgtUtils.httpPost(requestUrl, data);

        LOGGER.debug(requestUrl + "?customernumber=" + zgtUtils.getCustomernumber() + "&data=" + data, module);

        // 第三步 判断请求是否成功，
        if (responseMap.containsKey("code")) {
            LOGGER.debug("3-cashpay request error" + responseMap, module);
            //request.setAttribute(ERROR_MESSAGE, "易宝付款请求异常-3");
            return "error";
        } else {
            LOGGER.debug("3-cashpay request success" + responseMap, module);
        }

        // 第四步 解密同步响应密文data，获取明文参数
        String responseData = responseMap.get("data");
        Map<String, String> responseDataMap = zgtUtils.decryptData(responseData);

        LOGGER.debug("4-cashpay sync response：" + responseMap, module);
        LOGGER.debug("data解密后明文：" + responseDataMap, module);

        // 第五步 code=1时，方表示接口处理成功
        if (!"1".equals(responseDataMap.get("code"))) {
            LOGGER.debug("code = " + responseDataMap.get("code") + "<br>", module);
            LOGGER.debug("msg  = " + responseDataMap.get("msg"), module);
            //request.setAttribute(ERROR_MESSAGE, "易宝付款请求异常-1");
            return "error";
        } else {
            LOGGER.debug("5-code response success", module);
        }

        // 第六步 hmac签名验证
        if (!zgtUtils.checkHmac(responseDataMap, zgtUtils.PAYAPI_RESPONSE_HMAC_ORDER)) {
            LOGGER.debug("6-hmac check error!", module);
            //request.setAttribute(ERROR_MESSAGE, "hmac异常-1");
            return "error";
        } else {
            LOGGER.debug("6-hmac check success", module);
        }


        LOGGER.debug("return  " + responseDataMap.get("payurl"), module);
        return responseDataMap.get("payurl");
    }

    /**
     * 易宝支付完成回调接口
     * @param data
     * @return
     */
    @Override
    public String yeePayNotify(String data) {
        ZGTUtils zgtUtils = new ZGTUtils(CUSTOMER_NUMBER, KEY_FOR_HMAC);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String clazzMethodName = module + "." + methodName;
        LOGGER.debug(clazzMethodName, module);

        String requestId = "";
        String msg = "";

        // 第一步 获取回调密文data
        //String data = request.getParameter("data");

        // 第二步 解密密文data，获取明文参数
        Map<String, String> dataMap = zgtUtils.decryptData(data);
        LOGGER.debug("易宝的同步响应：" + data, module);
        LOGGER.debug("data解密后明文：" + dataMap, module);
        if (dataMap == null) {
            return "易宝付款请求回调异常";
        }

        requestId = dataMap.get("requestid");

        // 第三步 hmac签名验证
        if (!zgtUtils.checkHmac(dataMap, zgtUtils.PAYAPICALLBACK_HMAC_ORDER)) {
//            AppLogUtil.log(getDispatcher(), "YEEPAY_REQUEST", requestId, "易宝支付请求回调异常：签名验证错误", clazzMethodName,
//                    userLogin.getString("userLoginId"), dataMap);
            LOGGER.debug("宝付款请求回调异常：签名验证错误" + dataMap, module);
            //request.setAttribute(ERROR_MESSAGE, "易宝付款请求回调异常");
            msg = "宝付款请求回调异常：签名验证错误";
            return yeePayNotify_return(msg);
        }

        // 第四步 判断通知方式，如果是后台通知则需要回写SUCCESS
        String notifytype = dataMap.get("notifytype");
        if ("SERVER".equals(notifytype)) {
        } /*
			 * else { request.setAttribute("dataMap", dataMap);
			 * RequestDispatcher view =
			 * request.getRequestDispatcher("jsp/payApiCallback.jsp");
			 * view.forward(request, response); }
			 */

        //更改支付状态为“已收到”

        //调用易宝接口查询fee
        queryOrderFromYeePay(requestId);

        msg = "success";
        return yeePayNotify_return(msg);

    }

    public String yeePayNotify_return(String msg){
        if(msg.equalsIgnoreCase("success")){
            return "success";
        }else{
            return msg;
        }
    }

    /**
     * 从易宝查询订单
     * @param requestid
     * @return
     */

    public String queryOrderFromYeePay(String requestid) {
        ZGTUtils zgtUtils = new ZGTUtils(CUSTOMER_NUMBER, KEY_FOR_HMAC);
        Map<String, String> params = new HashMap<String, String>();
        params.put("requestid", requestid);

        // 第一步 生成密文data
        String data = zgtUtils.buildData(params, zgtUtils.QUERYORDERAPI_REQUEST_HMAC_ORDER);

        // 第二步 发起请求
        String requestUrl = zgtUtils.getRequestUrl(zgtUtils.QUERYORDERAPI_NAME);
        Map<String, String> responseMap = zgtUtils.httpPost(requestUrl, data);
        LOGGER.debug(requestUrl + "?customernumber=" + zgtUtils.getCustomernumber() + "&data=" + data, module);

        // 第三步 判断请求是否成功，
        if (responseMap.containsKey("code")) {
            LOGGER.debug("3-cashpay query order request error" + responseMap, module);
            return "查询订单错误";
        } else {
            LOGGER.debug("3-cashpay query order request success" + responseMap, module);
        }

        // 第四步 解密同步响应密文data，获取明文参数
        String responseData = responseMap.get("data");
        Map<String, String> responseDataMap = zgtUtils.decryptData(responseData);

        LOGGER.debug("4-cashpay sync response：" + responseMap, module);
        LOGGER.debug("data解密后明文：" + responseDataMap, module);

        // 第五步 code=1时，方表示接口处理成功
        if (!"1".equals(responseDataMap.get("code"))) {
            LOGGER.debug("code = " + responseDataMap.get("code") + "<br>", module);
            LOGGER.debug("msg  = " + responseDataMap.get("msg"), module);
            return "查询订单错误";
        } else {
            LOGGER.debug("5-code response success", module);
        }

        // 第六步 hmac签名验证
        if (!zgtUtils.checkHmac(responseDataMap, zgtUtils.QUERYORDERAPI_RESPONSE_HMAC_ORDER)) {
            LOGGER.debug("6-hmac check error!", module);
            return "查询订单签名验证错误";
        } else {
            LOGGER.debug("6-hmac check success", module);
        }

        BigDecimal actualAmount = new BigDecimal(responseDataMap.get("amount"));
        String feeStr = "0";
        if(null != responseDataMap.get("fee")){
            if(responseDataMap.get("fee").trim().length() > 0){
                feeStr = responseDataMap.get("fee");
            }
        }
        BigDecimal fee = new BigDecimal(feeStr);

        String requestId = responseDataMap.get("requestid");
        //存储数据fee

        return "success";
    }


    //----------------------------------测试相关----------------------------------------------------
    //----------------------------------测试相关----------------------------------------------------

    /**
     * 支付完成后台通知测试接口
     * @request
     * @response
     */
    public String yeepayNotifyTest(Long id, Long userId) {
        ZGTUtils zgtUtils = new ZGTUtils(CUSTOMER_NUMBER, KEY_FOR_HMAC);

        //查找purchase记录

        Map<String, String> params = new HashMap<String, String>();
        params.put("requestid", requestId);
        params.put("code", "1");
        params.put("notifytype", "server");
        params.put("externalid", "1111111111111");
        params.put("amount", "0.01");
        params.put("cardno", "1111");
        params.put("bankcode", "CMBCHINA");
        params.put("cardtype", "DEBIT");
        params.put("paydate", "");

        // 第一步 生成密文data
        String data = zgtUtils.buildData(params, zgtUtils.NOTIFYAPI_HMAC_ORDER);
        LOGGER.debug("data = " + data, module);

        // 第二步 发起请求
        String requestUrl = "http://localhost:8080/front/pay/yeepay/notify.do";
        Map<String, String> responseMap = zgtUtils.httpGet(requestUrl, data);
        LOGGER.debug(requestUrl + "?customernumber=" + zgtUtils.getCustomernumber() + "&data=" + data, module);

        return responseMap.get("obj");
    }


}