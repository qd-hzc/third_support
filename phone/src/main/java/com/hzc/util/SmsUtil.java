package com.hzc.util;

import com.hzc.sms.client.JsonReqClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 调用hzc-sms.1.0.jar，发送短信demo
 * String templateId = "3853";//短信模板id
 * String code = SmsUtil.sendMsg(phone, templateId);
 * Created by HZC on 2015/5/19.
 */
public class SmsUtil {

    /**
     * 发送手机短信
     * 返回发送到该手机短信验证码
     * 说明：
     * 对于第三方服务云之讯，是否已经成功发送验证码，这里没有做任何处理
     * new JsonReqClient().sendSms做的只是已经通知云之讯服务可以发送验证码到手机了
     *
     * @param phone      手机号
     * @param templateId 短信模板id
     * @return
     */
    public static String sendMsg(String phone, String templateId) throws Exception {
        //云之讯提供的用户参数
        String accountSid = "8d41f8301363b5ea99294d600d79ab20";
        String token = "25d3464c932c585882a931170223ef88";
        String appId = "0c5b05bd992e40d58e17cf282ede0495";

        String code = generateWord();
        String s = new JsonReqClient().sendSms(accountSid, token, appId, templateId, phone, code);
        return code;
    }

    /**
     * 产生随机的6位数字字符串
     *
     * @return
     */
    private static String generateWord() {
        int length = 6;
        String[] beforeShuffle = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        List list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        return afterShuffle.substring(2, 2 + length);
    }
}
