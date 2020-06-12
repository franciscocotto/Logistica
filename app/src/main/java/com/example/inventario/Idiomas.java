package com.example.inventario;

public class Idiomas {
    private int id_idioma;
    private String idioma;

    public Idiomas() {
    }

    public Idiomas(int id_idioma, String idioma) {
        this.id_idioma = id_idioma;
        this.idioma = idioma;
    }

    public int getId_idioma() {
        return id_idioma;
    }

    public void setId_idioma(int id_idioma) {
        this.id_idioma = id_idioma;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
}
