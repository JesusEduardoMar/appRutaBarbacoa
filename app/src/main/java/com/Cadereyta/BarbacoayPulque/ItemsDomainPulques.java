package com.Cadereyta.BarbacoayPulque;

import java.io.Serializable;

public class ItemsDomainPulques implements Serializable {

    private String idPulque;

    //private String nombre_vinedos;
    private String nombre_pulque;
    private String ubicacion_pulque;
    private String horario_pulque;

    private String url;


    public String getIdPulque() {
        return idPulque;
    }

    public void setIdPulque(String idPulque) {
        this.idPulque = idPulque;
    }

    public String getNombre_pulque() {
        return nombre_pulque;
    }

    public void setNombre_barbacoa(String nombre_barbacoa) {
        this.nombre_pulque = nombre_pulque;
    }

    public String getUbicacion_pulque() {
        return ubicacion_pulque;
    }

    public void setUbicacion_pulque(String ubicacion_pulque) {
        this.ubicacion_pulque = ubicacion_pulque;
    }

    public String getHorario_pulque() {
        return horario_pulque;
    }

    public void setHorario_pulque(String horario_pulque) {
        this.horario_pulque = horario_pulque;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public ItemsDomainPulques(String idPulque, String nombre_pulque, String ubicacion_pulque, String horario_pulque, String url) {
        this.idPulque = idPulque;
        this.nombre_pulque = nombre_pulque;
        this.ubicacion_pulque = ubicacion_pulque;
        this.horario_pulque = horario_pulque;
        this.url = url;
    }
    public ItemsDomainPulques() {
        // Constructor vacío requerido para Firestore
    }
}
