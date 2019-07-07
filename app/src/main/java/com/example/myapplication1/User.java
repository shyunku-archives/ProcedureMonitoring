package com.example.myapplication1;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String deviceID = "TESTCODE1";
    public ProcedurePack pack = new ProcedurePack();

    public User(){
    }

    public User(String ID){
        this.deviceID = ID;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", this.deviceID);
        result.put("procedures", this.pack);

        return result;
    }
}
