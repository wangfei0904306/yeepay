package com.example.yeepay;


import com.example.yeepay.vo.CallYeePayVo;

/**
 * 贷款人信息表业务接口
 */
public interface FinPaymentService {

    /**
     * 调用易宝支付
     * @param bean
     * @return
     */

    public String callYeePay(CallYeePayVo bean);

    /**
     * 易宝后台回调
     *
     * @param data
     * @return
     */
    public String yeePayNotify(String data);

    /**
     * 易宝后台回调--测试
     *
     * @param id, userId
     * @return
     */
    public String yeepayNotifyTest(Long id, Long userId);
}
