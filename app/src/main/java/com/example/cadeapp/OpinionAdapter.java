package com.example.cadeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class OpinionAdapter extends RecyclerView.Adapter<OpinionAdapter.OpinionViewHolder> {

    private List<Opinion> listaOpiniones;

    public OpinionAdapter(List<Opinion> listaOpiniones) {
        this.listaOpiniones = listaOpiniones;
    }

    @NonNull
    @Override
    public OpinionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new OpinionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpinionViewHolder holder, int position) {
        Opinion opinion = listaOpiniones.get(position);
        holder.nombreUsuarioTextView.setText(opinion.getNombreUsuario());
        holder.comentarioTextView.setText(opinion.getComentario());
        holder.calificacionRatingBar.setRating(opinion.getCalificacion());
        DateFormat formatoFecha = new SimpleDateFormat("dd/mm/yyyy");
        String fecha = formatoFecha.format(opinion.getFecha());
        holder.fechaComentario.setText(fecha);
    }

    @Override
    public int getItemCount() {
        return listaOpiniones.size();
    }

    public static class OpinionViewHolder extends RecyclerView.ViewHolder {
        TextView nombreUsuarioTextView;
        TextView comentarioTextView;
        RatingBar calificacionRatingBar;
        TextView fechaComentario;

        public OpinionViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreUsuarioTextView = itemView.findViewById(R.id.nombreUsuarioTextView);
            comentarioTextView = itemView.findViewById(R.id.comentarioTextView);
            calificacionRatingBar = itemView.findViewById(R.id.calificacionRatingBar);
            fechaComentario = itemView.findViewById(R.id.fechaComentario);
        }
    }
}
