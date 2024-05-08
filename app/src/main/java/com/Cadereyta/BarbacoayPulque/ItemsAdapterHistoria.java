package com.Cadereyta.BarbacoayPulque;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemsAdapterHistoria extends RecyclerView.Adapter<ItemsAdapterHistoria.ViewHolder> {

    private List<String> items;
    private Context context;

    public ItemsAdapterHistoria(List<String> items, Context context){
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemsAdapterHistoria.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_viewholdercardviews,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //ItemsDomainHistoria itemsDomainHistoria = items.get(position);
        Glide.with(context).load(items.get(position)).into(holder.pic);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic=itemView.findViewById(R.id.url);
        }
    }
}
