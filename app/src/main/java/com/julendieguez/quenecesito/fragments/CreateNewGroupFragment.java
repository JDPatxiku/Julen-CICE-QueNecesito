package com.julendieguez.quenecesito.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.julendieguez.quenecesito.MainMenuActivity;
import com.julendieguez.quenecesito.R;
import com.julendieguez.quenecesito.database.DatabaseFunctions;
import com.julendieguez.quenecesito.database.Group;
import com.julendieguez.quenecesito.database.User;

import java.util.HashMap;
import java.util.Map;

public class CreateNewGroupFragment extends Fragment {
    private MainMenuActivity parentActivity;
    private Button btnCreateList;
    private EditText txtName, txtDescription;
    private ImageView imgList;
    private View inflatedView;
    private ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_create_new_group, container, false);
        setReferences();
        setValues();
        return inflatedView;
    }
    private void setReferences(){
        imgList = (ImageView) inflatedView.findViewById(R.id.listImg);
        txtName = (EditText) inflatedView.findViewById(R.id.txtName);
        txtDescription = (EditText) inflatedView.findViewById(R.id.txtDescription);
        btnCreateList = (Button) inflatedView.findViewById(R.id.btnCreateList);
        btnCreateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewList();
            }
        });
        pDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
        pDialog.setIndeterminate(true);
        pDialog.setMessage(getString(R.string.createListDialog));
    }

    private void setValues(){
        parentActivity = (MainMenuActivity)getActivity();
        parentActivity.changeMenuValues(false);
    }

    private void setNewList(){
        if(!checkIfNameIsEmpty()){
            if(!checkIfDescriptionIsEmpty()) {
                pDialog.show();
                createNewList();
            }else{
                Toast.makeText(getContext(),getString(R.string.createListDescriptionLengthError),Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getContext(),getString(R.string.createListNameLengthError),Toast.LENGTH_LONG).show();
        }
    }

    private void createNewList(){
        String uniqueKey = updateGroup(parentActivity.getmDatabase());
        updateUser(parentActivity.getmDatabase(),uniqueKey);
    }

    private String updateGroup(FirebaseDatabase db){
        Group group = new Group(txtName.getText().toString(),txtDescription.getText().toString());
        group.setId(db.getReference("groups").push().getKey());
        group.setUsers(DatabaseFunctions.getUserUID(FirebaseAuth.getInstance()));
        DatabaseFunctions.updateGroup(FirebaseAuth.getInstance(),db,group);
        return group.obtainId();
    }

    private void updateUser(final FirebaseDatabase db, final String uniqueKey){
        DatabaseReference ref = db.getReference("users/"+DatabaseFunctions.getUserUID(FirebaseAuth.getInstance()));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userBackUp = DatabaseFunctions.fillUserWithDataSnapshot(dataSnapshot);
                userBackUp.addGroup(uniqueKey);
                Map<String, Object> users = new HashMap<String, Object>();
                users.put(DatabaseFunctions.getUserUID(FirebaseAuth.getInstance()),userBackUp);
                DatabaseReference userRef = db.getReference("users");
                userRef.updateChildren(users);
                pDialog.dismiss();
                changeFragment();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                pDialog.hide();
            }
        });
    }
    private void changeFragment(){
        getFragmentManager().popBackStack();
    }
    private boolean checkIfNameIsEmpty(){
        return txtName.getText().toString().length() == 0;
    }
    private boolean checkIfDescriptionIsEmpty(){return txtDescription.getText().toString().length() == 0;}

}
