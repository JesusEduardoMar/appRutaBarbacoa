package com.Cadereyta.BarbacoayPulque;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class OpinionAdapter extends RecyclerView.Adapter<OpinionAdapter.OpinionViewHolder> {

    private List<Opinion> listaOpiniones;
    private FirebaseFirestore db;

    public OpinionAdapter(List<Opinion> listaOpiniones) {
        this.listaOpiniones = listaOpiniones;
    }

    @NonNull
    @Override
    public OpinionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);

        // Iniciar instancia para firestore
        db = FirebaseFirestore.getInstance();

        return new OpinionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpinionViewHolder holder, int position) {
        Opinion opinion = listaOpiniones.get(position);
        String idUsuario = opinion.getIdUsuario();

        // Extraer el nombre el usuario con el Id
        db.collection("usuarios").document(idUsuario)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String nombreUsuario = document.getString("nombre");
                            holder.nombreUsuarioTextView.setText(nombreUsuario);
                        } else {
                            Log.d("TAG", "Usuario no encontrado");
                        }
                    } else {
                        Log.w("TAG", "No se puede obtener el nombre del usuario:", task.getException());
                    }
                });

        holder.comentarioTextView.setText(opinion.getComentario());
        holder.calificacionRatingBar.setRating(opinion.getCalificacion());
        DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = formatoFecha.format(opinion.obtenerFecha());
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
