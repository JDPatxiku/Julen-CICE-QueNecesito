package com.julendieguez.quenecesito.fragments;


import android.os.Bundle;
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
import com.julendieguez.quenecesito.database.User;
import com.julendieguez.quenecesito.recyclerviewcomponents.GroupAdapter;
import com.julendieguez.quenecesito.recyclerviewcomponents.SwipeActions;

import java.util.List;

public class GroupListFragment extends Fragment {
    private static MainMenuActivity parentActivity;
    private User userData;
    private List<Group> userGroups;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GroupAdapter gAdapter;


    private void getGroupDataFromDB(FirebaseDatabase db){
        DatabaseReference groupDataReference = db.getReference("groups");
        groupDataReference.keepSynced(true);
        groupDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userGroups = DatabaseFunctions.getUserGroupLists(dataSnapshot,userData.getGroups());
                if(userGroups != null)
                    fillListWithGroups();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public MainMenuActivity getParentActivity(){
        return parentActivity;
    }
    private void fillListWithGroups(){
        gAdapter = new GroupAdapter(userGroups, this);
        recyclerView.setAdapter(gAdapter);
    }

    public void removeGroupFromUser(String gID){
        userData.removeGroup(gID);
        DatabaseFunctions.updateUser(FirebaseAuth.getInstance(), parentActivity.getmDatabase(), userData);
    }
    public void removeUserFromGroup(Group g){
        g.removeUser(DatabaseFunctions.getUserUID(FirebaseAuth.getInstance()));
        if(!g.anyUserIn())
            g.prepareToDelete();
        DatabaseFunctions.updateGroup(FirebaseAuth.getInstance(), parentActivity.getmDatabase(), g);
    }
    public GroupListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_group_list, container, false);
        setValues(inflatedView);
        return inflatedView;
    }
    private void setValues(View v){
        if(parentActivity == null)
            parentActivity = (MainMenuActivity)getActivity();
        userData = parentActivity.getUserData();
        if(userData != null)
            getGroupDataFromDB(parentActivity.getmDatabase());
        parentActivity.changeMenuType(true);
        parentActivity.changeMenuValues(false);
        recyclerView = (RecyclerView) v.findViewById(R.id.groupRecycler);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        SwipeActions.setUpGroupSwipeDelete(parentActivity,recyclerView,v);
    }

    public void setUserData(User u){
        userData = u;
        getGroupDataFromDB(parentActivity.getmDatabase());
    }
}
