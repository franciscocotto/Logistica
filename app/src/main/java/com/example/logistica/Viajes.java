package com.example.logistica;

public class Viajes {

    private int id_viaje, id_ruta, id_vehiculo, id_conductor;
    private String inicio, finalizacion, nomViaje;

    public Viajes(){}

    public Viajes(int id_viaje, int id_ruta, int id_vehiculo, int id_conductor, String inicio, String finalizacion, String nomViaje) {
        this.id_viaje = id_viaje;
        this.id_ruta = id_ruta;
        this.id_vehiculo = id_vehiculo;
        this.id_conductor = id_conductor;
        this.inicio = inicio;
        this.finalizacion = finalizacion;
        this.nomViaje = nomViaje;
    }

    public int getId_viaje() {
        return id_viaje;
    }

    public int getId_ruta() {
        return id_ruta;
    }

    public int getId_vehiculo() {
        return id_vehiculo;
    }

    public int getId_conductor() {
        return id_conductor;
    }

    public String getInicio() {
        return inicio;
    }

    public String getFinalizacion() {
        return finalizacion;
    }

    public String getNomViaje() {
        return nomViaje;
    }


    public void setId_viaje(int id_viaje) {
        this.id_viaje = id_viaje;
    }

    public void setId_ruta(int id_ruta) {
        this.id_ruta = id_ruta;
    }

    public void setId_vehiculo(int id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }

    public void setId_conductor(int id_conductor) {
        this.id_conductor = id_conductor;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public void setFinalizacion(String finalizacion) {
        this.finalizacion = finalizacion;
    }

    public void setNomViaje(String nomViaje) {
        this.nomViaje = nomViaje;
    }
}
