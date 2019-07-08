package com.shyunku.myapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    public DialogFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Dialog");
        View view = inflater.inflate(R.layout.pop_up, container);
        return view;
    }
}
