package com.example.cadeapp;

import java.io.Serializable;

public class ItemsDomainEventos implements Serializable {

    private String idEvento;
    private String nombre_evento;
    private String ubicacion_evento;
    private String url;
    private String fecha_evento; // Cambiado a tipo String para almacenar la fecha como texto

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public String getNombre_evento() {
        return nombre_evento;
    }

    public void setNombre_evento(String nombre_evento) {
        this.nombre_evento = nombre_evento;
    }

    public String getUbicacion_evento() {
        return ubicacion_evento;
    }

    public void setUbicacion_evento(String ubicacion_evento) {
        this.ubicacion_evento = ubicacion_evento;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFecha_evento() {
        return fecha_evento;
    }

    public void setFecha_evento(String fecha_evento) {
        this.fecha_evento = fecha_evento;
    }

    public ItemsDomainEventos(String idEvento, String nombre_evento, String ubicacion_evento, String url, String fecha_evento) {
        this.idEvento = idEvento;
        this.nombre_evento = nombre_evento;
        this.ubicacion_evento = ubicacion_evento;
        this.url = url;
        this.fecha_evento = fecha_evento;
    }

    public ItemsDomainEventos() {
        // Constructor vacío requerido para Firestore
    }
}
