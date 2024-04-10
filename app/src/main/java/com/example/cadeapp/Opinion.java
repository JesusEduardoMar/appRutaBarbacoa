package com.example.cadeapp;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;

public class Opinion {
    private String nombreUsuario;
    private String comentario;
    private float calificacion;
    private Object timestamp;
    private String idBarbacoa;
    private String idEvento;
    private Date fecha;

    // Constructor vacío requerido para Firestore
    public Opinion() {
    }

    // Constructor para comentarios de barbacoas o eventos
    public Opinion(String nombreUsuario, String comentario, float calificacion, String idBarbacoa, String idEvento) {
        this.nombreUsuario = nombreUsuario;
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.idBarbacoa = idBarbacoa;
        this.idEvento = idEvento;
        this.timestamp = FieldValue.serverTimestamp();
    }

    // Constructor para comentarios de barbacoas o eventos
    public Opinion(String nombreUsuario, String comentario, float calificacion, String idBarbacoa, String idEvento, Date fecha) {
        this.nombreUsuario = nombreUsuario;
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.idBarbacoa = idBarbacoa;
        this.idEvento = idEvento;
        this.timestamp = FieldValue.serverTimestamp();
        this.fecha = fecha;
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

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
