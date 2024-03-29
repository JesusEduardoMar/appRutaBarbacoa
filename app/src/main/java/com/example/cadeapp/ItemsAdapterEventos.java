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

import java.util.ArrayList;

public class ItemsAdapterEventos extends RecyclerView.Adapter<ItemsAdapterEventos.ViewHolder> {
    private ArrayList<ItemsDomainEventos> items;
    private Context context;

    public ItemsAdapterEventos(ArrayList<ItemsDomainEventos> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsDomainEventos item = items.get(position);
        holder.titleTxt.setText(item.getNombre_evento());
        holder.addressTxt.setText(item.getUbicacion_evento());
        //holder.dateTxt.setText(item.getFecha_evento());

        Glide.with(context).load(item.getUrl()).into(holder.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar un Toast con el nombre del evento
                Toast.makeText(context, item.getNombre_evento(), Toast.LENGTH_SHORT).show();

                // Abrir la actividad de detalle del evento
                Intent intent = new Intent(context, DetailEventosActivity.class);
                intent.putExtra("idEvento", item.getIdEvento()); // Pasa el ID del evento a la actividad de detalle
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, addressTxt, dateTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.nombrevinedo);
            addressTxt = itemView.findViewById(R.id.direccion);
            //dateTxt = itemView.findViewById(R.id.fecha_evento);
            pic = itemView.findViewById(R.id.url);

        }
    }
}
