package com.example.cadeapp;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;

public class Opinion {
    private String idUsuario;
    private String comentario;
    private float calificacion;
    private Object timestamp;
    private String idBarbacoa, idEvento, idPulque;
    public Date fecha;

    // Constructor vacío requerido para Firestore
    public Opinion() {
    }

    // Constructor para comentarios de barbacoas o eventos
    public Opinion(String idUsuario, String comentario, float calificacion, String idBarbacoa, String idEvento, String idPulque) {
        this.idUsuario = idUsuario;
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.idBarbacoa = idBarbacoa;
        this.idEvento = idEvento;
        this.idPulque = idPulque;
        this.timestamp = FieldValue.serverTimestamp();
    }

    // Constructor para comentarios de barbacoas o eventos
    public Opinion(String idUsuario, String comentario, float calificacion, String idBarbacoa, String idEvento,  String idPulque, Date fecha) {
        this.idUsuario = idUsuario;
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.idBarbacoa = idBarbacoa;
        this.idEvento = idEvento;
        this.idPulque = idPulque;
        this.timestamp = FieldValue.serverTimestamp();
        this.fecha = fecha;
    }

    // Getter and setter methods for the fields

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getIdPulque() {
        return idPulque;
    }

    public void setIdPulque(String idPulque) {
        this.idPulque = idPulque;
    }
}
