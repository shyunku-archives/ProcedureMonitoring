package com.shyunku.myapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{
    public ArrayList<Procedure> procedures;
    public Context mContext;
    private String deviceKey = "";
    String deviceID = "";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

    public void removeItem(int position) {
        procedures.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, procedures.size());
    }

    public MyRecyclerAdapter(ArrayList<Procedure> procs, String id){
        this.procedures = procs;
        this.deviceID = id;
        this.setKey();

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
        timer.schedule(timerTask, 0, 250);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        mContext = viewGroup.getContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int pos){
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

        final int position = pos;
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabaseEngine.getFreshLocalDB().getReference("Devices/"+deviceKey+"/procedures");
                final DatabaseReference refs = FirebaseDatabaseEngine.getFreshLocalDB().getReference("Devices/"+deviceKey+"/procedures");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.child("registerFlag").getValue().equals(procedures.get(position).getRegisterFlag().getTimeInMillis() + "")) {
                                refs.child(data.getKey()).removeValue();
                                removeItem(position);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        holder.modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View popupView = View.inflate(mContext, R.layout.pop_up, null);
                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);

                final ConstraintLayout container = new ConstraintLayout(mContext);
                final ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = mContext.getResources().getDimensionPixelSize(R.dimen.checkbox_margin);
                params.rightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.checkbox_margin);
                popupView.setLayoutParams(params);
                container.addView(popupView);

                final Switch mySwitch = popupView.findViewById(R.id.switch1);
                final TextView startFlagView = popupView.findViewById(R.id.startFlag);
                final TextView endFlagView = popupView.findViewById(R.id.endFlag);
                final TextView procName = popupView.findViewById(R.id.procName);
                final TextView errorText = popupView.findViewById(R.id.error);
                mySwitch.setChecked(false);
                startFlagView.setText(sdf.format(new Date()));
                startFlagView.setEnabled(true);

                Procedure sel = procedures.get(position);
                procName.setText(sel.getProcedureName());
                startFlagView.setText(sdf.format(sel.getStartFlag().getTimeInMillis()));
                endFlagView.setText(sdf.format(sel.getEndFlag().getTimeInMillis()));

                mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            Date date = new Date();
                            date.setTime(System.currentTimeMillis());
                            startFlagView.setText(sdf.format(new Date()));
                            startFlagView.setEnabled(false);
                        }else
                            startFlagView.setEnabled(true);
                    }
                });

                builder.setTitle("Procedure 수정");
                builder.setView(container);
                builder.setCancelable(true);

                builder.setPositiveButton("저장",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((ViewGroup)container.getParent()).removeView(container);
                            }
                        });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        ((ViewGroup)container.getParent()).removeView(container);
                    }
                });
                final androidx.appcompat.app.AlertDialog dialog = builder.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if (procName.getText().length() == 0) {
                            errorText.setText("프로시저 이름을 입력해주세요.");
                            return;
                        }
                        if (startFlagView.getText().length() == 0) {
                            errorText.setText("시작 날짜/시간을 입력해주세요.");
                            return;
                        }
                        if(!isValidDate(startFlagView.getText().toString())){
                            errorText.setText("시작 날짜/시간의 형식이 올바르지 않습니다.");
                            return;
                        }
                        if (endFlagView.getText().length() == 0) {
                            errorText.setText("종료 날짜/시간을 입력해주세요.");
                            return;
                        }
                        if(!isValidDate(endFlagView.getText().toString())){
                            errorText.setText("종료 날짜/시간의 형식이 올바르지 않습니다.");
                            return;
                        }

                        Procedure proc = null;
                        try {
                            if (sdf.parse(startFlagView.getText().toString()).getTime() > sdf.parse(endFlagView.getText().toString()).getTime()) {
                                errorText.setText("종료 시점이 시작 시점보다 앞서 있습니다.");
                                return;
                            }

                            proc = new Procedure(
                                    procName.getText().toString(),
                                    procedures.get(position).getRegisterFlag(),
                                    newCalendar(sdf.parse(startFlagView.getText().toString())),
                                    newCalendar(sdf.parse(endFlagView.getText().toString()))
                            );
                        }catch(Exception e){}finally{
                            final Procedure procc = proc;
                            procedures.set(position, proc);

                            final DatabaseReference ref = FirebaseDatabaseEngine.getFreshLocalDB().getReference("Devices/"+deviceKey+"/procedures");
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot data : dataSnapshot.getChildren()){
                                        if(data.child("registerFlag").getValue().equals(procedures.get(position).getRegisterFlag().getTimeInMillis()+"")){
                                            ref.child(data.getKey()).child("procName").setValue(procc.getProcedureName());
                                            ref.child(data.getKey()).child("startFlag").setValue(procc.getStartFlag().getTimeInMillis()+"");
                                            ref.child(data.getKey()).child("endFlag").setValue(procc.getEndFlag().getTimeInMillis()+"");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
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

        public ImageButton modifyButton;
        public ImageButton deleteButton;

        public View total;
        public ViewHolder(View view){
            super(view);
            total = view;
            contentView = (TextView)view.findViewById(R.id.textView);
            progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
            percentage = (TextView)view.findViewById(R.id.textView2);
            remained = (TextView)view.findViewById(R.id.textView3);

            modifyButton = (ImageButton)view.findViewById(R.id.imageButton);
            deleteButton = (ImageButton)view.findViewById(R.id.imageButton2);
        }
    }

    private boolean isValidDate(String str){
        try{
            sdf.setLenient(false);
            sdf.parse(str);
            return  true;
        }catch (ParseException e){
            return  false;
        }
    }

    private Calendar newCalendar(Date parsed){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsed);
        return calendar;
    }

    public void setKey(){
        DatabaseReference ref = FirebaseDatabaseEngine.getFreshLocalDB().getReference("Devices");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean searched = false;
                if(dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        String idCheck = snapshot.child("id").getValue().toString();
                        if (idCheck.equals(deviceID)) {
                            registerKey(snapshot.getKey());
                            searched = true;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void toast(String str){
        Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
    }

    private void registerKey(String key){
        this.deviceKey = key;
    }
}
