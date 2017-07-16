package com.julendieguez.quenecesito.recyclerviewcomponents;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.julendieguez.quenecesito.R;
import com.julendieguez.quenecesito.database.Group;
import com.julendieguez.quenecesito.fragments.GroupListFragment;

import java.util.List;

public class GroupAdapter extends  RecyclerView.Adapter<GroupHolder>{
    private List<Group> groups;
    private View view;
    private GroupListFragment parent;
    public GroupAdapter(List<Group> groups, GroupListFragment parent){
        this.groups = groups;
        this.parent = parent;
    }
    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_row, parent, false);
        return new GroupHolder(view,this.parent);
    }

    @Override
    public void onBindViewHolder(GroupHolder holder, int position) {
        Group g = groups.get(position);
        holder.setValues(g);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


    public void remove(int position){
        parent.removeGroupFromUser(groups.get(position).obtainId());
        parent.removeUserFromGroup(groups.get(position));
        groups.remove(position);
        notifyItemRemoved(position);
    }
}

class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private TextView name, description, iconLetter;
    private Group g;
    private GroupListFragment parent;

    public GroupHolder(View itemView, GroupListFragment parent) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.parent = parent;
        name = (TextView) itemView.findViewById(R.id.rowHeader);
        description = (TextView) itemView.findViewById(R.id.rowBody);
        iconLetter = (TextView) itemView.findViewById(R.id.groupNameLetter);
    }

    public void setValues(Group g){
        this.g = g;
        name.setText(g.getName());
        description.setText(g.getDescription());
        setInitial(g.getName());
    }
    private void setInitial(String n){
        iconLetter.setText(" "+n.substring(0,1).toUpperCase()+" ");
    }
    @Override
    public void onClick(View v) {
        if(g != null){
            parent.getParentActivity().changeToItemFragment(g);
        }
    }



}