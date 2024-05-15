package com.Cadereyta.BarbacoayPulque;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

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
        // Configuración de resolucion para Glide
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(500,500);

        Glide.with(context)
                .asBitmap() // Cargar como un bitmap para la carga progresiva
                .load(items.get(position))
                .thumbnail(0.20f)
                .placeholder(R.drawable.cargandooo) // Cargamos una imagen de baja resolución inicialmente
                .error(R.drawable.borrego_error) //Imagen en caso de error al cargar
                .apply(requestOptions) // Aplicar opciones de cache
                .transition(BitmapTransitionOptions.withCrossFade()) // Agregar transición al cargar la imagen
                .into(holder.pic);

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
