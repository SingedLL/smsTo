package com.simple.sms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.simple.sms.adapter.SenderAdapter;
import com.simple.sms.model.SenderModel;
import com.simple.sms.model.vo.EmailSettingVo;
import com.simple.sms.utils.sender.SenderMailMsg;
import com.simple.sms.utils.sender.SenderUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.simple.sms.model.SenderModel.STATUS_ON;
import static com.simple.sms.model.SenderModel.TYPE_EMAIL;


public class SenderActivity extends Activity {

    private String TAG = "SenderActivity";
    // 用于存储数据
    private List<SenderModel> senderModels = new ArrayList<>();
    private SenderAdapter adapter;
    public static final int NOTIFY = 0x9731993;
    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NOTIFY:
                    Toast.makeText(SenderActivity.this, msg.getData().getString("DATA"), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        SenderUtil.init(SenderActivity.this);

        // 先拿到数据并放在适配器上
        initSenders(); //初始化数据
        adapter = new SenderAdapter(SenderActivity.this, R.layout.sender_item, senderModels);

        // 将适配器上的数据传递给listView
        ListView listView = findViewById(R.id.list_view_sender);
        listView.setAdapter(adapter);

        // 为ListView注册一个监听器，当用户点击了ListView中的任何一个子项时，就会回调onItemClick()方法
        // 在这个方法中可以通过position参数判断出用户点击的是那一个子项
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SenderModel senderModel = senderModels.get(position);
                Log.d(TAG, "onItemClick: "+senderModel);

                switch (senderModel.getType()){
                    case TYPE_EMAIL:
                        setEmail(senderModel);
                        break;
                    default:
                        Toast.makeText(SenderActivity.this,"异常的发送方类型！删除",Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(SenderActivity.this);

                builder.setMessage("确定删除?");
                builder.setTitle("提示");

                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SenderUtil.delSender(senderModels.get(position).getId());
                        initSenders();
                        adapter.del(senderModels);
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
    private void initSenders() {
        senderModels = SenderUtil.getSender(null, null);
        ;
    }

    public void addSender(View view) {
        setEmail(null);
        // AlertDialog.Builder builder = new AlertDialog.Builder(SenderActivity.this);
        // builder.setTitle("选择发送方类型");
        // builder.setItems(R.array.add_sender_menu, new DialogInterface.OnClickListener() {//添加列表
        //     @Override
        //     public void onClick(DialogInterface dialogInterface, int which) {
        //         switch (which) {
        //             case TYPE_EMAIL:
        //                 setEmail(null);
        //                 break;
        //             default:
        //                 Toast.makeText(SenderActivity.this, "暂不支持这种转发！", Toast.LENGTH_LONG).show();
        //                 break;
        //         }
        //     }
        // });
        // builder.show();
    }


    private void setEmail(final SenderModel senderModel) {
        EmailSettingVo emailSettingVo = null;
        //try phrase json setting
        if (senderModel != null) {
            String jsonSettingStr = senderModel.getJsonSetting();
            if (jsonSettingStr != null) {
                emailSettingVo = JSON.parseObject(jsonSettingStr, EmailSettingVo.class);
            }
        }

        final AlertDialog.Builder alertDialog71 = new AlertDialog.Builder(SenderActivity.this);
        View view1 = View.inflate(SenderActivity.this, R.layout.activity_alter_dialog_setview_email, null);

        final EditText editTextEmailName = view1.findViewById(R.id.editTextEmailName);
        if (senderModel != null) editTextEmailName.setText(senderModel.getName());
        final EditText editTextEmailHost = view1.findViewById(R.id.editTextEmailHost);
        if (emailSettingVo != null) editTextEmailHost.setText(emailSettingVo.getHost());
//        final EditText editTextEmailPort = view1.findViewById(R.id.editTextEmailPort);
//        if (emailSettingVo != null) editTextEmailPort.setText(emailSettingVo.getPort());

//        final Switch switchEmailSSl = view1.findViewById(R.id.switchEmailSSl);
//        if (emailSettingVo != null) switchEmailSSl.setChecked(emailSettingVo.getSsl());
        final EditText editTextEmailFromAdd = view1.findViewById(R.id.editTextEmailFromAdd);
        if (emailSettingVo != null) editTextEmailFromAdd.setText(emailSettingVo.getFromEmail());
        final EditText editTextEmailPsw = view1.findViewById(R.id.editTextEmailPsw);
        if (emailSettingVo != null) editTextEmailPsw.setText(emailSettingVo.getPwd());
        final EditText editTextEmailToAdd = view1.findViewById(R.id.editTextEmailToAdd);
        if (emailSettingVo != null) editTextEmailToAdd.setText(emailSettingVo.getToEmail());

        Button buttonemailok = view1.findViewById(R.id.buttonemailok);
        Button buttonemaildel = view1.findViewById(R.id.buttonemaildel);
        Button buttonemailtest = view1.findViewById(R.id.buttonemailtest);
        alertDialog71
                .setTitle(R.string.setemailtitle)
                .setIcon(R.drawable.ic_baseline_email_24)
                .setView(view1)
                .create();
        final AlertDialog show = alertDialog71.show();

        buttonemailok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String host = editTextEmailHost.getText().toString();
                String fromemail = editTextEmailFromAdd.getText().toString();
                String pwd = editTextEmailPsw.getText().toString();
                String toemail = editTextEmailToAdd.getText().toString();

                if (host.isEmpty() || fromemail.isEmpty() || pwd.isEmpty() || toemail.isEmpty()) {
                    Toast.makeText(SenderActivity.this, "所有内容不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if (senderModel == null) {
                    SenderModel newSenderModel = new SenderModel();
                    newSenderModel.setName(editTextEmailName.getText().toString());
                    newSenderModel.setType(TYPE_EMAIL);
                    newSenderModel.setStatus(STATUS_ON);
                    EmailSettingVo emailSettingVonew = new EmailSettingVo(host,fromemail,pwd,toemail);
                    newSenderModel.setJsonSetting(JSON.toJSONString(emailSettingVonew));
                    SenderUtil.addSender(newSenderModel);
                    initSenders();
                    adapter.add(senderModels);
                } else {
                    senderModel.setName(editTextEmailName.getText().toString());
                    senderModel.setType(TYPE_EMAIL);
                    senderModel.setStatus(STATUS_ON);
                    EmailSettingVo emailSettingVonew = new EmailSettingVo(host,fromemail,pwd,toemail);
                    senderModel.setJsonSetting(JSON.toJSONString(emailSettingVonew));
                    SenderUtil.updateSender(senderModel);
                    initSenders();
                    adapter.update(senderModels);
                }


                show.dismiss();
            }
        });
        buttonemaildel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (senderModel != null) {
                    SenderUtil.delSender(senderModel.getId());
                    initSenders();
                    adapter.del(senderModels);
                }
                show.dismiss();
            }
        });
        buttonemailtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String host = editTextEmailHost.getText().toString(); //"smtp.qq.com"
//                String port = "465";//editTextEmailPort.getText().toString();
//                Boolean ssl = switchEmailSSl.isChecked();
                String fromemail = editTextEmailFromAdd.getText().toString(); 
                String pwd = editTextEmailPsw.getText().toString(); 
                String toemail = editTextEmailToAdd.getText().toString(); 
                if (!host.isEmpty() && !fromemail.isEmpty() && !pwd.isEmpty() && !toemail.isEmpty()) {
                    try {
                        SenderMailMsg.sendEmail(handler,host, fromemail,pwd,toemail,"test", "test@" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
                    } catch (Exception e) {
                        Toast.makeText(SenderActivity.this, "发送失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SenderActivity.this, "所有内容不能为空", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
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
