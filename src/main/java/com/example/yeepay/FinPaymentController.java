package com.example.yeepay;

import com.example.yeepay.utils.JsonResult;
import com.example.yeepay.vo.CallYeePayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

/**
 * 支付控制器
 * @author wangfei
 */
@RestController
@RequestMapping("/")
public class FinPaymentController implements RestController {

    @Autowired
    private FinPaymentService paymentService;

    //@Log("调用易宝支付")
    @RequestMapping(value = "/yeepay", method = {RequestMethod.GET, RequestMethod.POST})
    public JsonResult callYeePay(HttpServletRequest request, HttpServletResponse response){

        String result = paymentService.callYeePay(new CallYeePayVo());

        if(result.contains("yeepay.com/")){
            return JsonResult.resultSuccess("调用易宝支付接口成功",result);
        }else{
            return JsonResult.resultError("调用易宝支付接口失败");
        }

    }

    //@Log("易宝支付完成回调接口")
    @RequestMapping(value = "yeepay/notify", method = RequestMethod.GET)
    public JsonResult yeepayNotify(@RequestParam("data") String data){

        String result = paymentService.yeePayNotify(data);

        if(result.equalsIgnoreCase("success")){
            return JsonResult.resultSuccess("回调易宝接口成功",result);
        }else{
            return JsonResult.resultError("回调易宝接口失败");
        }
    }

    //@Log("易宝支付完成回调接口--测试")
    @RequestMapping(value = "yeepay/notify/test", method = RequestMethod.GET)
    public JsonResult yeepayNotifyTest(@RequestParam("id") Long id, @RequestParam("userId") Long userId){

        String result = paymentService.yeepayNotifyTest(id, userId);

        if(result.equalsIgnoreCase("success")){
            return JsonResult.resultSuccess("回调易宝接口成功--测试",result);
        }else{
            return JsonResult.resultError("回调易宝接口失败--测试");
        }
    }


    @Override
    public String value() {
        return null;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
