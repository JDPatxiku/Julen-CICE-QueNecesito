package com.julendieguez.quenecesito.recyclerviewcomponents;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.julendieguez.quenecesito.R;
import com.julendieguez.quenecesito.database.Item;
import com.julendieguez.quenecesito.fragments.ItemListFragment;

import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    private List<Item> items;
    private View view;
    private ItemListFragment parent;

    public ItemAdapter(List<Item> items, ItemListFragment parent){
        this.items = items;
        this.parent = parent;
    }
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ItemHolder(view, this.parent);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Item i = items.get(position);
        holder.setValues(i);
    }
    @Override
    public int getItemCount() {return items.size();}

    public void remove(int position){
        parent.removeItemFromGroup(items.get(position).obtainId());
        parent.deleteItem(items.get(position).obtainId());
        items.remove(position);
        notifyItemRemoved(position);
    }
}

class ItemHolder extends RecyclerView.ViewHolder{
    private Item i;
    private ItemListFragment parent;
    private TextView txtName, txtBrand, txtQuantity, txtPrice;
    public ItemHolder(View itemView, ItemListFragment parent) {
        super(itemView);
        this.parent = parent;
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtBrand = (TextView) itemView.findViewById(R.id.txtBrand);
        txtQuantity = (TextView) itemView.findViewById(R.id.txtQuantity);
        txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
    }


    public void setValues(Item i){
        this.i = i;
        txtName.setText(parent.getString(R.string.itemListName) + " " + i.getName());
        if(!i.getBrand().equals("undefined"))
            txtBrand.setText(parent.getString(R.string.itemListBrand) + " " + i.getBrand());
        else
            txtBrand.setText("");
        if(i.getPrice() != 0)
            txtPrice.setText(String.valueOf(i.getPrice()*i.getQuantity()) + "€");
        else
            txtPrice.setText("");
        txtQuantity.setText(i.getQuantity()+"X");
    }
}
