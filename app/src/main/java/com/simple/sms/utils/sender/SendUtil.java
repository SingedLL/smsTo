package com.simple.sms.utils.sender;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.simple.sms.model.LogModel;
import com.simple.sms.model.RuleModel;
import com.simple.sms.model.SenderModel;
import com.simple.sms.model.vo.EmailSettingVo;
import com.simple.sms.model.vo.SmsVo;
import com.simple.sms.utils.LogUtil;
import com.simple.sms.utils.RuleUtil;
import com.simple.sms.utils.SettingUtil;

import java.util.List;

import static com.simple.sms.model.SenderModel.TYPE_EMAIL;

public class SendUtil {
    private static String TAG = "SendUtil";

    public static void send_msg(String msg){
        if(SettingUtil.using_email()){
//            SenderMailMsg.send(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_TOADD_KEY),"转发",msg);
        }

    }
    public static void send_msg_list(Context context, List<SmsVo> smsVoList){
        Log.i(TAG, "send_msg_list size: "+smsVoList.size());
        for (SmsVo smsVo:smsVoList){
            SendUtil.send_msg(context,smsVo);
        }
    }
    public static void send_msg(Context context, SmsVo smsVo){
        Log.i(TAG, "send_msg smsVo:"+smsVo);
        RuleUtil.init(context);
        LogUtil.init(context);

        List<RuleModel> rulelist = RuleUtil.getRule(null,null);
        if(!rulelist.isEmpty()){
            SenderUtil.init(context);
            for (RuleModel ruleModel:rulelist
            ) {
                Log.v("匹配规则", JSON.toJSONString(ruleModel));
                if (ruleModel.getChose()) {
                    Log.v("当前匹配规则", JSON.toJSONString(ruleModel));
                    try{
                        if(ruleModel.checkMsg(smsVo)){
                            List<SenderModel> senderModels = SenderUtil.getSender(ruleModel.getSenderId(),null);
                            for (SenderModel senderModel:senderModels
                            ) {
                                LogUtil.addLog(new LogModel(smsVo.getMobile(),smsVo.getContent(),senderModel.getId(),JSON.toJSONString(smsVo.getSmsExtraVo())));
                                SendUtil.senderSendMsgNoHandError(smsVo,senderModel);
                            }
                        }else {
                            Log.v("短信规则不匹配", "不发送短信");
                        }
                    }catch (Exception e){
                        Log.e(TAG, "send_msg: fail checkMsg:",e);
                    }

                }
            }

        }
    }
    public static void sendMsgByRuleModelSenderId(final Handler handError, RuleModel ruleModel, SmsVo smsVo, Long senderId) throws Exception {
        if(senderId==null){
            throw new Exception("先新建并选择发送方");
        }

        if(!ruleModel.checkMsg(smsVo)){
            throw new Exception("短信未匹配中规则");
        }

        List<SenderModel> senderModels = SenderUtil.getSender(senderId,null);
        if(senderModels.isEmpty()){
            throw new Exception("未找到发送方");
        }

        for (SenderModel senderModel:senderModels
        ) {
             //test
            //LogUtil.addLog(new LogModel(smsVo.getMobile(),smsVo.getContent(),senderModel.getId(),JSON.toJSONString(smsVo.getSmsExtraVo())));
            SendUtil.senderSendMsg(handError,smsVo,senderModel);
        }
    }
    public static void senderSendMsgNoHandError(SmsVo smsVo,SenderModel senderModel) {
        SendUtil.senderSendMsg(null,smsVo,senderModel);
    }
    public static void senderSendMsg(Handler handError, SmsVo smsVo, SenderModel senderModel) {

        Log.i(TAG, "senderSendMsg smsVo:"+smsVo+"senderModel:"+senderModel);
        switch (senderModel.getType()){
            case TYPE_EMAIL:
                //try phrase json setting
                if (senderModel.getJsonSetting() != null) {
                    EmailSettingVo emailSettingVo = JSON.parseObject(senderModel.getJsonSetting(), EmailSettingVo.class);
                    if(emailSettingVo!=null){
                        try {
                            SenderMailMsg.sendEmail(handError, emailSettingVo.getHost(),emailSettingVo.getFromEmail(),
                                    emailSettingVo.getPwd(),emailSettingVo.getToEmail(),smsVo.getMobile(),smsVo.getSmsVoForSend());
                        }catch (Exception e){
                            Log.e(TAG, "senderSendMsg: SenderMailMsg error "+e.getMessage() );
                        }

                    }
                }
                break;
            default:
                break;
        }
    }

}
