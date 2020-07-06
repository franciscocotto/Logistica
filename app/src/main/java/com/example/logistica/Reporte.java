package com.example.logistica;

public class Reporte {
    private int id_viaje;
    private String nombreRuta, origenRuta, destinoRuta, codVehiculo, placaVehiculo, duiConductor, nombreConductor,
                    apellidoConductor, licenciaConductor, inicioViaje, finalViaje, nombreViaje;

    public Reporte(){}

    public Reporte(int id_viaje, String nombreRuta, String origenRuta, String destinoRuta, String codVehiculo, String placaVehiculo, String duiConductor, String nombreConductor, String apellidoConductor, String licenciaConductor, String inicioViaje, String finalViaje, String nombreViaje) {
        this.id_viaje = id_viaje;
        this.nombreRuta = nombreRuta;
        this.origenRuta = origenRuta;
        this.destinoRuta = destinoRuta;
        this.codVehiculo = codVehiculo;
        this.placaVehiculo = placaVehiculo;
        this.duiConductor = duiConductor;
        this.nombreConductor = nombreConductor;
        this.apellidoConductor = apellidoConductor;
        this.licenciaConductor = licenciaConductor;
        this.inicioViaje = inicioViaje;
        this.finalViaje = finalViaje;
        this.nombreViaje = nombreViaje;
    }

    public int getId_viaje() {
        return id_viaje;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public String getOrigenRuta() {
        return origenRuta;
    }

    public String getDestinoRuta() {
        return destinoRuta;
    }

    public String getCodVehiculo() {
        return codVehiculo;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public String getDuiConductor() {
        return duiConductor;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public String getApellidoConductor() {
        return apellidoConductor;
    }

    public String getLicenciaConductor() {
        return licenciaConductor;
    }

    public String getInicioViaje() {
        return inicioViaje;
    }

    public String getFinalViaje() {
        return finalViaje;
    }

    public String getNombreViaje() {
        return nombreViaje;
    }


    public void setId_viaje(int id_viaje) {
        this.id_viaje = id_viaje;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public void setOrigenRuta(String origenRuta) {
        this.origenRuta = origenRuta;
    }

    public void setDestinoRuta(String destinoRuta) {
        this.destinoRuta = destinoRuta;
    }

    public void setCodVehiculo(String codVehiculo) {
        this.codVehiculo = codVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public void setDuiConductor(String duiConductor) {
        this.duiConductor = duiConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public void setApellidoConductor(String apellidoConductor) {
        this.apellidoConductor = apellidoConductor;
    }

    public void setLicenciaConductor(String licenciaConductor) {
        this.licenciaConductor = licenciaConductor;
    }

    public void setInicioViaje(String inicioViaje) {
        this.inicioViaje = inicioViaje;
    }

    public void setFinalViaje(String finalViaje) {
        this.finalViaje = finalViaje;
    }

    public void setNombreViaje(String nombreViaje) {
        this.nombreViaje = nombreViaje;
    }
}
