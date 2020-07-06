package com.example.logistica.ui.driver;

public class Licencia {
    private int id_lic;
    private String licencia;

    public Licencia() {
    }

    public Licencia(int id_lic, String licencia) {
        this.id_lic = id_lic;
        this.licencia = licencia;
    }

    public int getId_lic() {
        return id_lic;
    }

    public void setId_lic(int id_lic) {
        this.id_lic = id_lic;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }
}
