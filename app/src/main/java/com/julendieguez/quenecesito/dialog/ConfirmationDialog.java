package com.julendieguez.quenecesito.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.julendieguez.quenecesito.R;
import com.julendieguez.quenecesito.recyclerviewcomponents.GroupAdapter;

/**
 * Created by Julen on 04/05/2017.
 */

public class ConfirmationDialog extends Dialog {
    private Activity current;
    private GroupAdapter currentGA;
    private int position;
    public ConfirmationDialog(@NonNull Activity activity, GroupAdapter g, int pos) {
        super(activity);
        current = activity;
        currentGA = g;
        position = pos;
    }
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setReferences();
    }
    private void setReferences(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirm_dialog);
        Button btnOK = (Button) findViewById(R.id.btnOk);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentGA.remove(position);
                currentGA.notifyItemRemoved(position);
                dismiss();
            }
        });
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentGA.notifyDataSetChanged();
            }
        });
    }
}
