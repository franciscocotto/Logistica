package com.example.logistica;

public class Ruta {
    private int id_ruta;
    private String nameruta;
    private Double latitudInicial;
    private Double longitudInicial;
    private Double latitudFinal;
    private Double longitudFinal;

    public Ruta() {
    }

    public Ruta(int id_ruta, String nameruta, Double latitudInicial, Double longitudInicial, Double latitudFinal, Double longitudFinal) {
        this.id_ruta = id_ruta;
        this.nameruta = nameruta;
        this.latitudInicial = latitudInicial;
        this.longitudInicial = longitudInicial;
        this.latitudFinal = latitudFinal;
        this.longitudFinal = longitudFinal;
    }

    public int getId_ruta() {
        return id_ruta;
    }

    public void setId_ruta(int id_ruta) {
        this.id_ruta = id_ruta;
    }

    public String getNameruta() {
        return nameruta;
    }

    public void setNameruta(String nameruta) {
        this.nameruta = nameruta;
    }

    public Double getLatitudInicial() {
        return latitudInicial;
    }

    public void setLatitudInicial(Double latitudInicial) {
        this.latitudInicial = latitudInicial;
    }

    public Double getLongitudInicial() {
        return longitudInicial;
    }

    public void setLongitudInicial(Double longitudInicial) {
        this.longitudInicial = longitudInicial;
    }

    public Double getLatitudFinal() {
        return latitudFinal;
    }

    public void setLatitudFinal(Double latitudFinal) {
        this.latitudFinal = latitudFinal;
    }

    public Double getLongitudFinal() {
        return longitudFinal;
    }

    public void setLongitudFinal(Double longitudFinal) {
        this.longitudFinal = longitudFinal;
    }
}
