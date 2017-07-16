package com.julendieguez.quenecesito.recyclerviewcomponents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.julendieguez.quenecesito.MainMenuActivity;
import com.julendieguez.quenecesito.R;
import com.julendieguez.quenecesito.contacts.Contact;
import com.julendieguez.quenecesito.dialog.AddNewUserDialog;
import com.julendieguez.quenecesito.fragments.UserContactsFragment;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {
    private UserContactsFragment parent;
    private View view;
    private List<Contact> contacts;

    public ContactAdapter(UserContactsFragment parent, List<Contact> contacts){
        this.parent = parent;
        this.contacts = contacts;
    }
    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new ContactHolder(view,this.parent);
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, int position) {
        Contact c = contacts.get(position);
        holder.setValues(c);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private UserContactsFragment parent;
    private TextView name, description, iconLetter;
    private Contact c;

    public ContactHolder(View itemView, UserContactsFragment parent) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.parent = parent;
        name = (TextView) itemView.findViewById(R.id.rowHeader);
        description = (TextView) itemView.findViewById(R.id.rowBody);
        iconLetter = (TextView) itemView.findViewById(R.id.contactNameLetter);
    }
    @Override
    public void onClick(View v) {
        AddNewUserDialog dialog = new AddNewUserDialog((MainMenuActivity)parent.getActivity(),this.c);
        dialog.show();
    }

    public void setValues(Contact c){
        this.c = c;
        name.setText(c.getName());
        description.setText(c.getNumber());
        setInitial(c.getName());
    }

    private void setInitial(String n){
        iconLetter.setText(" "+n.substring(0,1).toUpperCase()+" ");
    }
}