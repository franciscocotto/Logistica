package com.example.logistica.ui.driver;

public class Licencia {
    private int id_lic;
    private String licencia;

    public Licencia() {
    }

    public Licencia(int id_idioma, String idioma) {
        this.id_lic = id_idioma;
        this.licencia = idioma;
    }

    public int getId_idioma() {
        return id_lic;
    }

    public void setId_idioma(int id_idioma) {
        this.id_lic = id_idioma;
    }

    public String getIdioma() {
        return licencia;
    }

    public void setIdioma(String idioma) {
        this.licencia = idioma;
    }
}
