package com.example.yeepay;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * FinPaymentController Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>���� 14, 2017</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)  //SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes=YeepayApplication.class)  //指定我们SpringBoot工程的Application启动类
@WebAppConfiguration  //由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration
public class FinPaymentControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private FinPaymentController finPaymentController;

    @Before
    public void before() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(finPaymentController).build();
    }

    @After
    public void after() throws Exception {

    }

    /**
     * Method: callYeePay(CallYeePayVo bean)
     */
    @Test
    public void testCallYeePay() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/yeepay");
        request.contentType(MediaType.APPLICATION_FORM_URLENCODED);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * Method: yeepayNotify(@RequestParam("data") String data)
     */
    @Test
    public void testYeepayNotify() throws Exception {
    //TODO: Test goes here...
    }

    /**
     * Method: yeepayNotifyTest(@RequestParam("id") Long id, @RequestParam("userId") Long userId)
     */
    @Test
    public void testYeepayNotifyTest() throws Exception {
    //TODO: Test goes here...
    }


} 
