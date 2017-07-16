package com.julendieguez.quenecesito.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.julendieguez.quenecesito.MainMenuActivity;
import com.julendieguez.quenecesito.R;
import com.julendieguez.quenecesito.contacts.Contact;
import com.julendieguez.quenecesito.database.DatabaseFunctions;
import com.julendieguez.quenecesito.database.Group;

public class AddNewUserDialog extends Dialog {
    private MainMenuActivity current;
    private Contact c;
    public AddNewUserDialog(@NonNull MainMenuActivity activity, Contact c) {
        super(activity);
        current = activity;
        this.c = c;
    }
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setReferences();
    }
    private void setReferences(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_new_user_dialog);
        Button btnOK = (Button) findViewById(R.id.btnOk);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current.getGroupData().setUsers(c.getuID());
                DatabaseFunctions.updateGroup(FirebaseAuth.getInstance(), FirebaseDatabase.getInstance(), current.getGroupData());
                DatabaseFunctions.updateOtherUser(c.getuID(),current.getmDatabase(),current.getGroupData().obtainId());
                current.getSupportFragmentManager().popBackStack();
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
        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
    }

    private void prepareToUpdateGroup(){

    }
}
