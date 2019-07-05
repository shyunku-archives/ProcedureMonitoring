package com.example.myapplication1;

import java.util.Calendar;

public class Procedure {
    private String procedureName;
    private Calendar registerFlag;
    private Calendar startFlag;
    private Calendar endFlag;

    public Procedure(String procedureName, Calendar registerFlag, Calendar startFlag, Calendar endFlag) {
        this.procedureName = procedureName;
        this.registerFlag = registerFlag;
        this.startFlag = startFlag;
        this.endFlag = endFlag;
    }



    public Calendar getRegisterFlag() {
        return registerFlag;
    }

    public void setRegisterFlag(Calendar registerFlag) {
        this.registerFlag = registerFlag;
    }

    public Calendar getStartFlag() {
        return startFlag;
    }

    public void setStartFlag(Calendar startFlag) {
        this.startFlag = startFlag;
    }

    public Calendar getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(Calendar endFlag) {
        this.endFlag = endFlag;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public double getProcedureProcessRate(){
        double rate;
        switch(this.getStatus()){
            case -1:
                rate = (double)(System.currentTimeMillis() - registerFlag.getTimeInMillis())/(double)(startFlag.getTimeInMillis() - registerFlag.getTimeInMillis());
                return refineProcessRate(rate);
            case 0:
                rate = (double)(System.currentTimeMillis() - startFlag.getTimeInMillis())/(double)(endFlag.getTimeInMillis() - startFlag.getTimeInMillis());
                return refineProcessRate(rate);
            case 1:
                return refineProcessRate(1);
            default:
                return 0;
        }
    }

    public int getStatus(){
        //-1은 아직 시작 안함, 0은 진행 중, 1은 끝남
        if(System.currentTimeMillis()<startFlag.getTimeInMillis())
            return -1;
        if(System.currentTimeMillis()>endFlag.getTimeInMillis())
            return 1;
        return 0;
    }

    public long getRemained(){
        long remained = 0;
        switch(this.getStatus()){
            case -1:
                remained = startFlag.getTimeInMillis() - System.currentTimeMillis();
                break;
            case 0:
                remained = endFlag.getTimeInMillis() - System.currentTimeMillis();
                break;
        }
        return remained;
    }

    private double refineProcessRate(double proc){
        if(proc>1) return 1;
        if(proc<0) return 0;
        return proc;
    }
}
