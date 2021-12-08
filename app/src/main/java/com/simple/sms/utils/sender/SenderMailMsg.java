package com.simple.sms.utils.sender;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import static com.simple.sms.SenderActivity.NOTIFY;


public class SenderMailMsg {
    private static String TAG = "SenderMailMsg";

    public static void sendEmail(final Handler handError, final String host, final String fromemail, final String pwd, final String toAdd, final String title, final String content) {

        Log.d(TAG, "sendEmail: host:" + host + " fromemail:" + fromemail + " pwd:" + pwd + " toAdd:" + toAdd);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    final MailSenderInfo mailInfo = new MailSenderInfo();
                    mailInfo.setMailServerHost(host);
                    mailInfo.setValidate(true);
                    mailInfo.setUserName(fromemail);  //你的邮箱地址
                    mailInfo.setPassword(pwd);//您的邮箱密码
                    mailInfo.setFromAddress(fromemail);//和上面username的邮箱地址一致
                    mailInfo.setToAddress(toAdd);
                    mailInfo.setSubject(title);
                    mailInfo.setContent(content);
//                    mailInfo.setSsl(ssl);

                    //这个类主要来发送邮件
                    // 判断是否需要身份认证
                    MyAuthenticator authenticator = null;
                    Properties pro = mailInfo.getProperties();
                    if (mailInfo.isValidate()) {
                        // 如果需要身份认证，则创建一个密码验证器
                        authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
                    }
                    // 根据邮件会话属性和密码验证器构造一个发送邮件的session
                    Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
                    try {
                        // 根据session创建一个邮件消息
                        final Message mailMessage = new MimeMessage(sendMailSession);
                        // 创建邮件发送者地址
                        Address from = new InternetAddress(mailInfo.getFromAddress());
                        // 设置邮件消息的发送者
                        mailMessage.setFrom(from);
                        // 创建邮件的接收者地址，并设置到邮件消息中
                        Address to = new InternetAddress(mailInfo.getToAddress());
                        mailMessage.setRecipient(Message.RecipientType.TO, to);
                        // 设置邮件消息的主题
                        mailMessage.setSubject(mailInfo.getSubject());
                        // 设置邮件消息发送的时间
                        mailMessage.setSentDate(new Date());
                        // 设置邮件消息的主要内容
                        String mailContent = mailInfo.getContent();
                        mailMessage.setText(mailContent);
                        // 发送邮件
                        Transport.send(mailMessage);
                        SendHistory.addHistory("Email mailInfo：" + mailInfo.toString());

                    } catch (MessagingException ex) {
                        SendHistory.addHistory("Email Fail mailInfo：" + mailInfo.toString());
                        ex.printStackTrace();
                        Log.e(TAG, "error" + ex.getMessage());
                        if (handError != null) {
                            android.os.Message msg = new android.os.Message();
                            msg.what = NOTIFY;
                            Bundle bundle = new Bundle();
                            bundle.putString("DATA", ex.getMessage());
                            msg.setData(bundle);
                            handError.sendMessage(msg);
                        }

                    }
                    if (handError != null) {
                        android.os.Message msg = new android.os.Message();
                        msg.what = NOTIFY;
                        Bundle bundle = new Bundle();
                        bundle.putString("DATA", "发送成功");
                        msg.setData(bundle);
                        handError.sendMessage(msg);
                    }

                    Log.e(TAG, "sendEmail success");//sms.sendHtmlMail(mailInfo);//发送html格式

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    if (handError != null) {
                        android.os.Message msg = new android.os.Message();
                        msg.what = NOTIFY;
                        Bundle bundle = new Bundle();
                        bundle.putString("DATA", e.getMessage());
                        msg.setData(bundle);
                        handError.sendMessage(msg);
                    }

                }
            }
        }).start();
    }

}

