package com.julendieguez.quenecesito.fragments;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.julendieguez.quenecesito.MainMenuActivity;
import com.julendieguez.quenecesito.R;
import com.julendieguez.quenecesito.database.DatabaseFunctions;
import com.julendieguez.quenecesito.database.User;


public class EditAccountFragment extends Fragment {
    private MainMenuActivity parentActivity;
    private TextView txtUsername;
    private EditText txtName;
    private Button btnEditName, btnEditAccount;
    private TextInputLayout txtNameContainer;
    private boolean shown;
    public EditAccountFragment() {
        shown = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_account, container, false);
        setValues();
        setReferences(view);
        return view;
    }

    private void setValues(){
        parentActivity = (MainMenuActivity)getActivity();
        parentActivity.changeMenuValues(false);
    }
    private void setReferences(View v){
        txtUsername = (TextView) v.findViewById(R.id.txtUsername);
        txtName = (EditText) v.findViewById(R.id.txtName);
        btnEditName = (Button) v.findViewById(R.id.btnEditName);
        btnEditAccount = (Button) v.findViewById(R.id.btnEditAccount);
        txtNameContainer = (TextInputLayout) v.findViewById(R.id.txtNameContainer);
        txtUsername.setText(parentActivity.getUserData().getName());
        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditFields();
            }
        });
        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserData();
            }
        });
    }
    private void showEditFields(){
        if(!shown) {
            txtNameContainer.setVisibility(View.VISIBLE);
            btnEditAccount.setVisibility(View.VISIBLE);
        }else{
            txtNameContainer.setVisibility(View.INVISIBLE);
            btnEditAccount.setVisibility(View.INVISIBLE);
        }
        shown = !shown;
    }

    private void changeUserData(){
        User u = parentActivity.getUserData();
        if(!checkIfNameIsEmpty()){
            u.setName(txtName.getText().toString());
            DatabaseFunctions.updateUser(FirebaseAuth.getInstance(),parentActivity.getmDatabase(),u);
            getFragmentManager().popBackStack();
        }
    }
    private  boolean checkIfNameIsEmpty(){
        return txtName.getText().toString().length() == 0;
    }
}
