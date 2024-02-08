package com.example.cadeapp;

import java.io.Serializable;

public class ItemsDomainVinedos implements Serializable {

    private String idBarbacoa;

    //private String nombre_vinedos;
    private String nombre_barbacoa;
    private String ubicacion_barbacoa;
    private String horario_barbacoa;

    private String url;


    public String getIdBarbacoa() {
        return idBarbacoa;
    }

    public void setIdBarbacoa(String idBarbacoa) {
        this.idBarbacoa = idBarbacoa;
    }

    public String getNombre_barbacoa() {
        return nombre_barbacoa;
    }

    public void setNombre_barbacoa(String nombre_barbacoa) {
        this.nombre_barbacoa = nombre_barbacoa;
    }

    public String getUbicacion_barbacoa() {
        return ubicacion_barbacoa;
    }

    public void setUbicacion_barbacoa(String ubicacion_barbacoa) {
        this.ubicacion_barbacoa = ubicacion_barbacoa;
    }

    public String getHorario_barbacoa() {
        return horario_barbacoa;
    }

    public void setHorario_barbacoa(String horario_barbacoa) {
        this.horario_barbacoa = horario_barbacoa;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public ItemsDomainVinedos(String idBarbacoa, String nombre_barbacoa, String ubicacion_barbacoa, String horario_barbacoa, String url) {
        this.idBarbacoa = idBarbacoa;
        this.nombre_barbacoa = nombre_barbacoa;
        this.ubicacion_barbacoa = ubicacion_barbacoa;
        this.horario_barbacoa = horario_barbacoa;
        this.url = url;
    }
    public ItemsDomainVinedos() {
        // Constructor vacío requerido para Firestore
    }
}
