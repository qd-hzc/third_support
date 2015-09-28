package com.hzc.ctrl;

import com.hzc.util.SmsUtil;
import com.hzc.util.alias.W;

/**
 * 短信服务
 * 云之讯
 * Created by HZC on 2015/8/02.
 */
public class SmSCtrl {

    /**
     * 发送验证码
     * （云之讯短信服务）
     */
    public void sendCode() throws Exception {
        String phone = W.getString("phone");
        String templateId = W.getString("templateId");//"3853";//短信模板id
        System.out.println(templateId);
        String code = SmsUtil.sendMsg(phone, templateId);
        W.writeJson(true, code);
    }

}
