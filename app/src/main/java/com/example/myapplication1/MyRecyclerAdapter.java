package com.example.myapplication1;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    public ArrayList<Procedure> procedures;
    public Context mContext;

    private int lastPosition = -1;

    public MyRecyclerAdapter(ArrayList<Procedure> procs, Context mContext){
        this.procedures = procs;
        this.mContext = mContext;


        final Handler handler = new Handler(){
            public void handleMessage(Message msg){
                updateList();
            }
        };

        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }

            @Override
            public boolean cancel() {
                return super.cancel();
            }
        };
        timer.schedule(timerTask, 0, 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos){
        holder.contentView.setText(procedures.get(pos).getProcedureName());
        holder.progressBar.setProgress((int)(procedures.get(pos).getProcedureProcessRate()*100000));
        holder.percentage.setText(String.format("%.13f%%", procedures.get(pos).getProcedureProcessRate()*100));
        switch(procedures.get(pos).getStatus()){
            case -1:
                holder.remained.setText("시작까지 "+remainedTimeFormatter((int) (procedures.get(pos).getRemained() / 1000)));
                break;
            case 0:
                holder.remained.setText("종료까지 "+remainedTimeFormatter((int) (procedures.get(pos).getRemained() / 1000)));
                break;
            case 1:
                holder.remained.setText("종료됨");
                break;
        }
    }

    private String remainedTimeFormatter(int remained){
        String res = "";
        int days = remained/86400;
        remained -= days*86400;
        int hours = remained/3600;
        remained -= hours*3600;
        int minutes = remained/60;
        remained -= minutes*60;
        int seconds = remained;

        if(days>0) res += " "+days+"일";
        if(hours>0) res += " "+hours+"시간";
        if(minutes>0) res += " "+minutes+"분";
        if(seconds>0) res += " "+seconds+"초";
        res += " 남음";
        return res;
    }

    public void updateList(){
        if(procedures != null && procedures.size()>0){
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount(){
        return procedures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView contentView;
        public ProgressBar progressBar;
        public TextView percentage;
        public TextView remained;

        public View total;
        public ViewHolder(View view){
            super(view);
            total = view;
            contentView = (TextView)view.findViewById(R.id.textView);
            progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
            percentage = (TextView)view.findViewById(R.id.textView2);
            remained = (TextView)view.findViewById(R.id.textView3);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation); lastPosition = position;
        }
    }
}
