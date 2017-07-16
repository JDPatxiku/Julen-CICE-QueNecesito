package com.julendieguez.quenecesito.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
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
import com.julendieguez.quenecesito.database.Item;

import java.util.HashMap;
import java.util.Map;

public class CreateNewItemFragment extends Fragment {
    private static MainMenuActivity parentActivity;
    private EditText txtName, txtBrand, txtPrice;
    private TextView txtQuantity;
    private int quantity;
    private SeekBar quantityBar;
    private Button btnCreateItem;

    public CreateNewItemFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_create_new_item, container, false);
        setValues();
        setReferences(inflatedView);
        return inflatedView;
    }

    private void setValues(){
        if(parentActivity == null)
            parentActivity = (MainMenuActivity)getActivity();
        parentActivity.changeMenuValues(false);
    }
    private void setReferences(View v){
        txtName = (EditText) v.findViewById(R.id.txtName);
        txtBrand = (EditText) v.findViewById(R.id.txtBrand);
        txtPrice = (EditText) v.findViewById(R.id.txtPrice);
        txtQuantity = (TextView) v.findViewById(R.id.txtQuantity);
        quantityBar = (SeekBar) v.findViewById(R.id.quantityBar);
        btnCreateItem = (Button) v.findViewById(R.id.btnCreateItem);
        quantityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == 0) {
                    quantity = 1;
                    seekBar.setProgress(0);
                    return;
                }
                if(progress % 5 == 0){
                    quantity = progress / 5;
                }
                txtQuantity.setText(getString(R.string.createItemQuantity) + String.valueOf(quantity));
                seekBar.setProgress(quantity * 5);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnCreateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItem();
            }
        });
        quantity = 1;
        txtQuantity.setText(getString(R.string.createItemQuantity)+String.valueOf(quantity));
    }

    private void setItem(){
        if(!checkIfNameIsEmpty()){
            createItem();
        }else{
            Toast.makeText(getContext(),getString(R.string.createItenNoNameError),Toast.LENGTH_LONG).show();
        }
    }

    private void createItem(){
        String uniqueKey = updateItems(parentActivity.getmDatabase());
        updateGroup(parentActivity.getmDatabase(), uniqueKey);
    }

    private String updateItems(FirebaseDatabase db){
        String brand = "undefined";
        float price = 0.0f;
        if(!txtBrand.getText().toString().equals(""))
            brand = txtBrand.getText().toString();
        if(!txtPrice.getText().toString().equals(""))
            price = Float.valueOf(txtPrice.getText().toString());
        Item item = new Item(DatabaseFunctions.getUserUID(FirebaseAuth.getInstance()),txtName.getText().toString(),brand,price,quantity);
        item.setId(db.getReference("items").push().getKey());
        DatabaseFunctions.updateItem(FirebaseAuth.getInstance(),db,item);
        return item.obtainId();
    }

    private void updateGroup(final FirebaseDatabase db, final String uniqueKey){
        DatabaseReference ref = db.getReference("groups/"+parentActivity.getGroupData().obtainId());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group groupBackUp = DatabaseFunctions.fillGroupWithDataSnapshot(dataSnapshot);
                groupBackUp.setItems(uniqueKey);
                Map<String, Object> groups = new HashMap<String, Object>();
                groups.put(groupBackUp.obtainId(),groupBackUp);
                DatabaseReference groupRef = db.getReference("groups");
                groupRef.updateChildren(groups);
                getFragmentManager().popBackStack();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private boolean checkIfNameIsEmpty(){
        return txtName.getText().toString().equals("");
    }
}
