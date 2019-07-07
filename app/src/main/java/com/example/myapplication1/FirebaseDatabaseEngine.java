package com.example.myapplication1;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseEngine {
    private final String TAG = "DB_ISSUE";
    static private FirebaseDatabase LocalDB = FirebaseDatabase.getInstance();
    static private DatabaseReference LocalDBref = FirebaseDatabase.getInstance().getReference();

    public FirebaseDatabaseEngine(){
    }

    public static void RenewDatabaseReference(){
        LocalDBref = FirebaseDatabase.getInstance().getReference();
    }

    public static void RenewDatabase(){
        LocalDB = FirebaseDatabase.getInstance();
    }


    //getter & setter
    public static DatabaseReference getFreshLocalDBref(){
        RenewDatabaseReference();
        return LocalDBref;
    }

    public static FirebaseDatabase getFreshLocalDB(){
        RenewDatabase();
        return LocalDB;
    }
}
