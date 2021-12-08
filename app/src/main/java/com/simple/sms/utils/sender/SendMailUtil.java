package com.simple.sms.utils.sender;

import android.util.Log;


import com.simple.sms.utils.Define;
import com.simple.sms.utils.SettingUtil;

import java.io.File;


public class SendMailUtil {
    private static String TAG = "SendMailUtil";

    public static void send(final File file, String toAdd, String title, String content){
        Log.d(TAG,"send file to "+toAdd);
        final MailInfo mailInfo = creatMail(toAdd,title,content);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendFileMail(mailInfo,file);
            }
        }).start();
    }

    public static void send(String toAdd, String title, String content){
        Log.d(TAG,"send to "+toAdd);
        final MailInfo mailInfo = creatMail(toAdd,title,content);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendTextMail(mailInfo);
            }
        }).start();
    }

    private static MailInfo creatMail(String toAdd, String title, String content) {
        Log.d(TAG,"creatMail to "+toAdd);
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_HOST_KEY));
        // mailInfo.setMailServerPort(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_PORT_KEY));
        mailInfo.setValidate(true);
        mailInfo.ssl(true);
        mailInfo.setUserName(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_FROMADD_KEY)); // 你的邮箱地址
        mailInfo.setPassword(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_PSW_KEY));// 您的邮箱密码
        mailInfo.setFromAddress(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_FROMADD_KEY)); // 发送的邮箱
        mailInfo.setToAddress(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_TOADD_KEY)); // 发到哪个邮件去
        mailInfo.setSubject(title); // 邮件主题
        mailInfo.setContent(content); // 邮件文本
        return mailInfo;
    }
}
