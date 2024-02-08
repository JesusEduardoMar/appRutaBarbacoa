package com.example.cadeapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cadeapp.R;

import java.util.ArrayList;

public class ItemsAdapterEventos extends  RecyclerView.Adapter<ItemsAdapterEventos.ViewHolder> {
    ArrayList<ItemsDomainEventos> items2;

    Context context;

    public ItemsAdapterEventos(ArrayList<ItemsDomainEventos> items2, Context context) {
        this.items2 = items2;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(context).inflate(R.layout.item_viewholder,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsDomainEventos itemsDomainEventos = items2.get(position);
        holder.titleTxt.setText(itemsDomainEventos.getNombre_evento());
        holder.addressTxt.setText(itemsDomainEventos.getUbicacion_evento());

        Glide.with(context).load(itemsDomainEventos.getUrl()).into(holder.pic);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,itemsDomainEventos.getNombre_evento(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetailFreixenetActivity.class);
                intent.putExtra("titleTxt", itemsDomainEventos.getNombre_evento());
                intent.putExtra("addressTxt", itemsDomainEventos.getUbicacion_evento());
                intent.putExtra("imageUrl", itemsDomainEventos.getUrl());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items2.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, addressTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.nombrevinedo);
            addressTxt=itemView.findViewById(R.id.direccion);
            pic=itemView.findViewById(R.id.url);
        }
    }
}
