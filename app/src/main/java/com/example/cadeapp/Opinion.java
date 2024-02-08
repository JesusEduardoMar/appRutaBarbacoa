package com.example.cadeapp;

import com.google.firebase.firestore.FieldValue;

public class Opinion {
    private String nombreUsuario;
    private String comentario;
    private float calificacion;
    private Object timestamp;
    private String idBarbacoa;

    // Constructor vacío requerido para Firestore
    public Opinion() {
    }

    public Opinion(String nombreUsuario, String comentario, float calificacion, String idBarbacoa) {
        this.nombreUsuario = nombreUsuario;
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.idBarbacoa = idBarbacoa;
        this.timestamp = FieldValue.serverTimestamp();
    }

    // Getter and setter methods for the fields

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getIdBarbacoa() {
        return idBarbacoa;
    }

    public void setIdBarbacoa(String idBarbacoa) {
        this.idBarbacoa = idBarbacoa;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}

