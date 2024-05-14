package com.Cadereyta.BarbacoayPulque;

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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class ItemsAdapterPulques extends  RecyclerView.Adapter<ItemsAdapterPulques.ViewHolder> {
    ArrayList<ItemsDomainPulques> items;
    private ArrayList<ItemsDomainPulques> itemsFull; // Lista original sin filtrar nada
    Context context;

    public ItemsAdapterPulques(ArrayList<ItemsDomainPulques> items, Context context) {
        this.items = items;
        this.itemsFull = new ArrayList<>(items); // Copia de la lista original
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(context).inflate(R.layout.item_viewholder_pulque,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsDomainPulques itemsDomainPulques = items.get(position);
        holder.titleTxt.setText(itemsDomainPulques.getNombre_pulque());
        holder.addressTxt.setText(itemsDomainPulques.getUbicacion_pulque());
        //holder.horarioTxt.setText(itemsDomainPulques.getHorario_pulque());

        Glide.with(context).load(itemsDomainPulques.getUrl()).into(holder.pic);
        // Configuración de cache para Glide
        /*RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Guardar imágenes originales y en formato decodificado en el cache

        Glide.with(context)
                .load(itemsDomainPulques.getUrl())
                .apply(requestOptions) // Aplicar opciones de cache
                .into(holder.pic);*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, itemsDomainPulques.getNombre_pulque(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetailPulqueActivity.class);
                intent.putExtra("idPulque", itemsDomainPulques.getIdPulque());  // Asegúrate de tener un método getIdPulque() en tu modelo
                intent.putExtra("titleTxt", itemsDomainPulques.getNombre_pulque());
                intent.putExtra("addressTxt", itemsDomainPulques.getUbicacion_pulque());
                //intent.putExtra("horarioTxt", itemsDomainPuques.getHorario_pulque());
                intent.putExtra("imageUrl", itemsDomainPulques.getUrl());

                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setFilter(ArrayList<ItemsDomainPulques> filteredList) {
        items = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, addressTxt; // horarioTxt
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.nombrepulque);
            addressTxt=itemView.findViewById(R.id.direccion);
            //horarioTxt = itemView.findViewById(R.id.horario);
            pic=itemView.findViewById(R.id.url);
        }
    }
}



