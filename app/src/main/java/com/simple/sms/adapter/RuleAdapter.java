package com.simple.sms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.simple.sms.R;
import com.simple.sms.model.RuleModel;
import com.simple.sms.model.SenderModel;
import com.simple.sms.utils.sender.SenderUtil;

import java.util.List;

public class RuleAdapter extends ArrayAdapter<RuleModel> {
    private int resourceId;
    private List<RuleModel> list;
    private OnChooseItemChanged choseListener;

    // 适配器的构造函数，把要适配的数据传入这里
    public RuleAdapter(Context context, int textViewResourceId, List<RuleModel> objects, OnChooseItemChanged choseListener){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
        list=objects;
        this.choseListener = choseListener;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public RuleModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        RuleModel item =list.get(position);
        if(item==null){
            return 0;
        }
        return item.getId();
    }
    // convertView 参数用于将之前加载好的布局进行缓存
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
       final  RuleModel ruleModel=getItem(position); //获取当前项的TLog实例

        // 加个判断，以免ListView每次滚动时都要重新加载布局，以提高运行效率
        View view;
        ViewHolder viewHolder;
        if (convertView==null){

            // 避免ListView每次滚动时都要重新加载布局，以提高运行效率
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            // 避免每次调用getView()时都要重新获取控件实例
            viewHolder=new ViewHolder();
            viewHolder.ruleMatch =view.findViewById(R.id.rule_match);
            viewHolder.ruleSender =view.findViewById(R.id.rule_sender);
            viewHolder.ruleChoose = view.findViewById(R.id.rule_choose);

            viewHolder.ruleChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choseListener.chooseItemChanged(ruleModel);
                }
            });

            // 将ViewHolder存储在View中（即将控件的实例存储在其中）
            view.setTag(viewHolder);
        } else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }

        // 获取控件实例，并调用set...方法使其显示出来
        if(ruleModel!=null){
            List<SenderModel> senderModel = SenderUtil.getSender(ruleModel.getSenderId(),null);
            viewHolder.ruleMatch.setText(ruleModel.getRuleMatch());
            if(!senderModel.isEmpty()){
                viewHolder.ruleSender.setText(senderModel.get(0).getName());

            }else{
                viewHolder.ruleSender.setText("");
            }
            viewHolder.ruleChoose.setChecked(ruleModel.getChose());
        }

        return view;
    }

    // 定义一个内部类，用于对控件的实例进行缓存
    class ViewHolder{
        RadioButton ruleChoose;
        TextView ruleMatch;
        TextView ruleSender;
    }
    public void refresh(List<RuleModel> ruleModels){
        if(list!=null){
            list=ruleModels;
            notifyDataSetChanged();
        }
    }
    public interface OnChooseItemChanged{
        public void chooseItemChanged(RuleModel ruleModel);
    }


}