package com.julendieguez.quenecesito.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.julendieguez.quenecesito.recyclerviewcomponents.ItemAdapter;
import com.julendieguez.quenecesito.recyclerviewcomponents.SwipeActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemListFragment extends Fragment {
    private String groupName;
    private MainMenuActivity parentActivity;
    private List<Item> itemGroup;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ItemAdapter iAdapter;
    private FloatingActionButton btnCreateItem;

    public ItemListFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_item_list, container, false);
        setValues(inflatedView);
        setReferences(inflatedView);
        return inflatedView;
    }
    private void setValues(View v){
        if(parentActivity == null)
            parentActivity = (MainMenuActivity)getActivity();
        parentActivity.changeMenuValues(true);
        if(groupName == null)
            groupName = parentActivity.getGroupData().getName();
        parentActivity.changeMenuType(false);
        parentActivity.setTitle(groupName);
        getGroupDataFromDB(parentActivity.getmDatabase());
    }
    private void setReferences(View v){
        btnCreateItem = (FloatingActionButton) v.findViewById(R.id.btnCreateItem);
        btnCreateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.changeToCreateItemFragment();
            }
        });
        recyclerView = (RecyclerView) v.findViewById(R.id.itemRecycler);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        SwipeActions.setUpItemSwipeDelete(parentActivity,recyclerView,v);
    }

    private void getGroupDataFromDB(final FirebaseDatabase db){
        DatabaseReference ref = db.getReference("groups/"+parentActivity.getGroupData().obtainId());
        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group g = DatabaseFunctions.fillGroupWithDataSnapshot(dataSnapshot);
                for(Map.Entry<String, String> entry : g.getUsers().entrySet()){
                    parentActivity.getGroupData().setUsers(entry.getValue());
                }
                if(g != null)
                    getItemDataFromDB(db,g);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getItemDataFromDB(FirebaseDatabase db, final Group g){
        DatabaseReference ref = db.getReference("items");
        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemGroup = DatabaseFunctions.getGroupItemList(dataSnapshot,g);
                if(itemGroup != null)
                    updateGroupItems();
                    fillListWithItems();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateGroupItems(){
        List<String> il = new ArrayList<String>();
        for(Item item: itemGroup){
            il.add(item.obtainId());
        }
        parentActivity.getGroupData().reasignItems(il);
    }
    public MainMenuActivity getParentActivity(){
        return parentActivity;
    }
    private void fillListWithItems(){
        iAdapter = new ItemAdapter(itemGroup, this);
        recyclerView.setAdapter(iAdapter);
    }

    public void removeItemFromGroup(String iID){
        parentActivity.getGroupData().removeItem(iID);
        DatabaseFunctions.updateGroup(FirebaseAuth.getInstance(),parentActivity.getmDatabase(),parentActivity.getGroupData());
    }
    public void deleteItem(String iID){
        DatabaseFunctions.deleteItem(FirebaseAuth.getInstance(),parentActivity.getmDatabase(),iID);
    }
}
