package com.example.logistica;

public class Vehiculos {
    private int id_vehiculo;
    private String cod_vehiculo, marca, modelo, tipo, url_img;

    public Vehiculos(){}

    public Vehiculos(int id_vehiculo, String cod_vehiculo, String marca, String modelo, String tipo, String url_img) {
        this.id_vehiculo = id_vehiculo;
        this.cod_vehiculo = cod_vehiculo;
        this.marca = marca;
        this.modelo = modelo;
        this.tipo = tipo;
        this.url_img = url_img;
    }

    public int getId_vehiculo() {
        return id_vehiculo;
    }

    public String getCod_vehiculo() {
        return cod_vehiculo;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getUrl_img() {
        return url_img;
    }


    public void setId_vehiculo(int id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }

    public void setCod_vehiculo(String cod_vehiculo) {
        this.cod_vehiculo = cod_vehiculo;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setUrl_img(String url_img) {
        this.url_img = url_img;
    }
}
