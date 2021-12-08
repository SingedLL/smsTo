package com.simple.sms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.simple.sms.adapter.RuleAdapter;
import com.simple.sms.model.RuleModel;
import com.simple.sms.model.SenderModel;
import com.simple.sms.model.vo.SmsExtraVo;
import com.simple.sms.model.vo.SmsVo;
import com.simple.sms.receiver.SMSBroadcastReceiver;
import com.simple.sms.utils.RuleUtil;
import com.simple.sms.utils.sender.SendUtil;
import com.simple.sms.utils.sender.SenderUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.simple.sms.SenderActivity.NOTIFY;

//参考
//https://github.com/xiaoyuanhost/TranspondSms

public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private SMSBroadcastReceiver smsBroadcastReceiver;
    private String TAG = "MainActivity";

    // 用于存储数据
    private List<RuleModel> ruleModels = new ArrayList<>();
    private RuleAdapter adapter;

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NOTIFY:
                    Toast.makeText(MainActivity.this, msg.getData().getString("DATA"), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        RuleUtil.init(MainActivity.this);
        SenderUtil.init(MainActivity.this);

        initRules(); //初始化数据
        adapter = new RuleAdapter(MainActivity.this, R.layout.rule_item, ruleModels, new RuleAdapter.OnChooseItemChanged() {
            @Override
            public void chooseItemChanged(RuleModel ruleModel) {
                if (ruleModel != null) {
                    for (int i=0; i<ruleModels.size(); i++) {
                        RuleModel curRuleModal = ruleModels.get(i);
                        curRuleModal.getId();
                        if (curRuleModal.getId().equals(ruleModel.getId()) && !curRuleModal.getChose()) {
                            curRuleModal.setChose(true);
                            RuleUtil.updateRule(curRuleModal);
                        } else if (curRuleModal.getChose() && !curRuleModal.getId().equals(ruleModel.getId())){
                            curRuleModal.setChose(false);
                            RuleUtil.updateRule(curRuleModal);
                        }
                    }

                    initRules();
                    adapter.refresh(ruleModels);
                }
            }
        });

        // 将适配器上的数据传递给listView
        ListView listView = findViewById(R.id.list_view_rule);
        listView.setAdapter(adapter);
        // 为ListView注册一个监听器，当用户点击了ListView中的任何一个子项时，就会回调onItemClick()方法
        // 在这个方法中可以通过position参数判断出用户点击的是那一个子项
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RuleModel ruleModel = ruleModels.get(position);
                Log.d(TAG, "onItemClick: "+ruleModel);
                setRule(ruleModel);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage("确定删除?");
                builder.setTitle("提示");

                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RuleUtil.delRule(ruleModels.get(position).getId());
                        initRules();
                        adapter.refresh(ruleModels);
                        Toast.makeText(getBaseContext(), "删除列表项", Toast.LENGTH_SHORT).show();
                    }
                });

                //添加AlertDialog.Builder对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
                return true;
            }
        });
    }

    // 初始化数据
    private void initRules() {
        ruleModels = RuleUtil.getRule(null, null);
    }

    public void addRule(View view) {
        setRule(null);
    }

    private void setRule(final RuleModel ruleModel) {
        final AlertDialog.Builder alertDialog71 = new AlertDialog.Builder(MainActivity.this);
        final View view1 = View.inflate(MainActivity.this, R.layout.activity_alter_dialog_setview_rule, null);

        final RadioGroup radioGroupRuleFiled = (RadioGroup) view1.findViewById(R.id.radioGroupRuleFiled);
        if(ruleModel!=null)radioGroupRuleFiled.check(ruleModel.getRuleFiledCheckId());

//        final RadioGroup radioGroupRuleCheck = (RadioGroup) view1.findViewById(R.id.radioGroupRuleCheck);
//        if(ruleModel!=null)radioGroupRuleCheck.check(ruleModel.getRuleCheckCheckId());

//        final TextView tv_mu_rule_tips = (TextView) view1.findViewById(R.id.tv_mu_rule_tips);
        final TextView ruleSenderTv = (TextView) view1.findViewById(R.id.ruleSenderTv);
        if(ruleModel!=null && ruleModel.getSenderId()!=null){
            List<SenderModel> getSeners = SenderUtil.getSender(ruleModel.getSenderId(),null);
            if(!getSeners.isEmpty()){
                ruleSenderTv.setText(getSeners.get(0).getName());
                ruleSenderTv.setTag(getSeners.get(0).getId());
            }
        }
        final Button btSetRuleSender = (Button) view1.findViewById(R.id.btSetRuleSender);
        btSetRuleSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"selectSender",Toast.LENGTH_LONG).show();
                selectSender(ruleSenderTv);
            }
        });

        final EditText editTextRuleValue = view1.findViewById(R.id.editTextRuleValue);
        if (ruleModel != null)
            editTextRuleValue.setText(ruleModel.getValue());

        //当更新选择的字段的时候，更新之下各个选项的状态
//        refreshSelectRadioGroupRuleFiled(radioGroupRuleFiled, radioGroupRuleCheck, editTextRuleValue,tv_mu_rule_tips);

        Button buttonruleok = view1.findViewById(R.id.buttonruleok);
        Button buttonruledel = view1.findViewById(R.id.buttonruledel);
        Button buttonruletest = view1.findViewById(R.id.buttonruletest);
        alertDialog71
                .setTitle(R.string.setrule)
                .setView(view1)
                .create();
        final AlertDialog show = alertDialog71.show();
        buttonruleok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object senderId = ruleSenderTv.getTag();
                if (ruleModel == null) {
                    RuleModel newRuleModel = new RuleModel();
                    newRuleModel.setFiled(RuleModel.getRuleFiledFromCheckId(radioGroupRuleFiled.getCheckedRadioButtonId()));
//                    newRuleModel.setCheck(RuleModel.getRuleCheckFromCheckId(radioGroupRuleCheck.getCheckedRadioButtonId()));
                    newRuleModel.setValue(editTextRuleValue.getText().toString());
                    if(senderId!=null){
                        newRuleModel.setSenderId(Long.valueOf(senderId.toString()));
                    }
                    RuleUtil.addRule(newRuleModel);
                    initRules();
                    adapter.refresh(ruleModels);
                } else {
                    ruleModel.setFiled(RuleModel.getRuleFiledFromCheckId(radioGroupRuleFiled.getCheckedRadioButtonId()));
//                    ruleModel.setCheck(RuleModel.getRuleCheckFromCheckId(radioGroupRuleCheck.getCheckedRadioButtonId()));
                    ruleModel.setValue(editTextRuleValue.getText().toString());
                    if(senderId!=null){
                        ruleModel.setSenderId(Long.valueOf(senderId.toString()));
                    }
                    RuleUtil.updateRule(ruleModel);
                    initRules();
                    adapter.refresh(ruleModels);
                }

                show.dismiss();


            }
        });
        buttonruledel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ruleModel != null) {
                    RuleUtil.delRule(ruleModel.getId());
                    initRules();
                    adapter.refresh(ruleModels);
                }
                show.dismiss();
            }
        });
        buttonruletest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object senderId = ruleSenderTv.getTag();
                if(senderId==null){
                    Toast.makeText(MainActivity.this,"请先创建选择发送方",Toast.LENGTH_LONG).show();
                }else{
                    if (ruleModel == null) {
                        RuleModel newRuleModel = new RuleModel();
                        newRuleModel.setFiled(RuleModel.getRuleFiledFromCheckId(radioGroupRuleFiled.getCheckedRadioButtonId()));
//                        newRuleModel.setCheck(RuleModel.getRuleCheckFromCheckId(radioGroupRuleCheck.getCheckedRadioButtonId()));
                        newRuleModel.setValue(editTextRuleValue.getText().toString());
                        newRuleModel.setSenderId(Long.valueOf(senderId.toString()));

                        testRule(newRuleModel,Long.valueOf(senderId.toString()));

                    } else {
                        ruleModel.setFiled(RuleModel.getRuleFiledFromCheckId(radioGroupRuleFiled.getCheckedRadioButtonId()));
//                        ruleModel.setCheck(RuleModel.getRuleCheckFromCheckId(radioGroupRuleCheck.getCheckedRadioButtonId()));
                        ruleModel.setValue(editTextRuleValue.getText().toString());
                        ruleModel.setSenderId(Long.valueOf(senderId.toString()));

                        testRule(ruleModel,Long.valueOf(senderId.toString()));

                    }



                }

            }
        });

    }




    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        //取消注册广播
        unregisterReceiver(smsBroadcastReceiver);
    }



    public void toSetting(){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }


    public void toSendSetting(View view){
        Intent intent = new Intent(this, SenderActivity.class);
        startActivity(intent);
    }


    //当更新选择的字段的时候，更新之下各个选项的状态
    // 如果设置了转发全部，禁用选择模式和匹配值输入
    // 如果设置了多重规则，选择模式置为是
//    private void refreshSelectRadioGroupRuleFiled(RadioGroup radioGroupRuleFiled, final RadioGroup radioGroupRuleCheck, final EditText editTextRuleValue, final TextView tv_mu_rule_tips){
//        refreshSelectRadioGroupRuleFiledAction(radioGroupRuleFiled.getCheckedRadioButtonId(),radioGroupRuleCheck,editTextRuleValue,tv_mu_rule_tips);
//
//        radioGroupRuleFiled.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                refreshSelectRadioGroupRuleFiledAction(checkedId,radioGroupRuleCheck,editTextRuleValue,tv_mu_rule_tips);
//            }
//        });
//    }

    private void refreshSelectRadioGroupRuleFiledAction(int checkedRuleFiledId, final RadioGroup radioGroupRuleCheck, final EditText editTextRuleValue, final TextView tv_mu_rule_tips){
        tv_mu_rule_tips.setVisibility(View.GONE);

        switch (checkedRuleFiledId){
            case R.id.btnTranspondAll:
                for(int i = 0; i < radioGroupRuleCheck.getChildCount(); i++){
                    ((RadioButton)radioGroupRuleCheck.getChildAt(i)).setEnabled(false);
                }
                editTextRuleValue.setEnabled(false);
                break;
//            case R.id.btnMultiMatch:
//                for(int i = 0; i < radioGroupRuleCheck.getChildCount(); i++){
//                    ((RadioButton)radioGroupRuleCheck.getChildAt(i)).setEnabled(false);
//                }
//                editTextRuleValue.setEnabled(true);
//                tv_mu_rule_tips.setVisibility(View.VISIBLE);
//                break;
            default:
                for(int i = 0; i < radioGroupRuleCheck.getChildCount(); i++){
                    ((RadioButton)radioGroupRuleCheck.getChildAt(i)).setEnabled(true);
                }
                editTextRuleValue.setEnabled(true);
                break;
        }
    }

    public void selectSender(final TextView showTv) {
        final List<SenderModel> senderModels = SenderUtil.getSender(null,null);
        if(senderModels.isEmpty()){
            Toast.makeText(MainActivity.this, "请先去设置发送方页面添加", Toast.LENGTH_SHORT).show();
            return;
        }
        final CharSequence[] senderNames= new CharSequence[senderModels.size()];
        for (int i=0;i<senderModels.size();i++){
            senderNames[i]=senderModels.get(i).getName();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择发送方");
        builder.setItems(senderNames, new DialogInterface.OnClickListener() {//添加列表
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Toast.makeText(MainActivity.this, senderNames[which], Toast.LENGTH_LONG).show();
                showTv.setText(senderNames[which]);
                showTv.setTag(senderModels.get(which).getId());
            }
        });
        builder.show();
    }

    public void testRule(final RuleModel ruleModel, final Long senderId) {
        final View view = View.inflate(MainActivity.this, R.layout.activity_alter_dialog_setview_rule_test, null);
        final EditText editTextTestPhone = (EditText) view.findViewById(R.id.editTextTestPhone);
        final EditText editTextTestMsgContent = (EditText)view.findViewById(R.id.editTextTestMsgContent);
        Button buttonruletest = view.findViewById(R.id.buttonruletest);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(MainActivity.this);
        ad1.setTitle("测试规则");
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(view);
        buttonruletest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("editTextTestPhone", editTextTestPhone.getText().toString());
                Log.i("editTextTestMsgContent", editTextTestMsgContent.getText().toString());

                try{
                    SmsVo testSmsVo=new SmsVo(editTextTestPhone.getText().toString(),editTextTestMsgContent.getText().toString(),new Date(),new SmsExtraVo(1,"啦啦啦","家用手机"));
                    SendUtil.sendMsgByRuleModelSenderId(handler,ruleModel,testSmsVo,senderId);

                }catch (Exception e){
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
        ad1.show();// 显示对话框
    }

    //按返回键不退出回到桌面
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void  checkPermission()
    {
        // 检查权限是否获取（android6.0及以上系统可能默认关闭权限，且没提示）
        PackageManager pm = getPackageManager();
        boolean permission_receive_boot = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.RECEIVE_BOOT_COMPLETED", this.getPackageName()));
        boolean permission_readsms = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.READ_SMS", this.getPackageName()));

        if (!(
                permission_receive_boot
                && permission_readsms
        )) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.READ_SMS,
            }, 0x01);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.to_setting:
                toSetting();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
