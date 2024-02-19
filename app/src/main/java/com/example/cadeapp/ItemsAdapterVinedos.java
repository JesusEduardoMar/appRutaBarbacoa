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

public class ItemsAdapterVinedos extends  RecyclerView.Adapter<ItemsAdapterVinedos.ViewHolder> {
    ArrayList<ItemsDomainVinedos> items;
    private ArrayList<ItemsDomainVinedos> itemsFull; // Lista original sin filtrar nada
    Context context;

    public ItemsAdapterVinedos(ArrayList<ItemsDomainVinedos> items, Context context) {
        this.items = items;
        this.itemsFull = new ArrayList<>(items); // Copia de la lista original
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
        ItemsDomainVinedos itemsDomainVinedos = items.get(position);
        holder.titleTxt.setText(itemsDomainVinedos.getNombre_barbacoa());
        holder.addressTxt.setText(itemsDomainVinedos.getUbicacion_barbacoa());
        //holder.horarioTxt.setText(itemsDomainVinedos.getHorario_barbacoa());

        Glide.with(context).load(itemsDomainVinedos.getUrl()).into(holder.pic);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, itemsDomainVinedos.getNombre_barbacoa(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetailFreixenetActivity.class);
                intent.putExtra("idBarbacoa", itemsDomainVinedos.getIdBarbacoa());  // Asegúrate de tener un método getIdBarbacoa() en tu modelo
                intent.putExtra("titleTxt", itemsDomainVinedos.getNombre_barbacoa());
                intent.putExtra("addressTxt", itemsDomainVinedos.getUbicacion_barbacoa());
                //intent.putExtra("horarioTxt", itemsDomainVinedos.getHorario_barbacoa());
                intent.putExtra("imageUrl", itemsDomainVinedos.getUrl());

                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setFilter(ArrayList<ItemsDomainVinedos> filteredList) {
        items = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, addressTxt; // horarioTxt
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.nombrevinedo);
            addressTxt=itemView.findViewById(R.id.direccion);
            //horarioTxt = itemView.findViewById(R.id.horario);
            pic=itemView.findViewById(R.id.url);
        }
    }
}
