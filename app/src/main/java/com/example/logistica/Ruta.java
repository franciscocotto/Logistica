package com.example.logistica;

public class Ruta {
    private int id_ruta, id_estado;
    private String nameruta, origen, destino;
    private String latitudInicial;
    private String longitudInicial;
    private String latitudFinal;
    private String longitudFinal;
    protected static String idRuta;
    public Ruta() {
    }

    public Ruta(int id_ruta,
                int id_estado,
                String nameruta,
                String origen,
                String destino,
                String latitudInicial,
                String longitudInicial,
                String latitudFinal,
                String longitudFinal) {
        this.id_ruta = id_ruta;
        this.id_estado = id_estado;
        this.nameruta = nameruta;
        this.origen = origen;
        this.destino = destino;
        this.latitudInicial = latitudInicial;
        this.longitudInicial = longitudInicial;
        this.latitudFinal = latitudFinal;
        this.longitudFinal = longitudFinal;
    }

    public static String getIdRuta() {
        return idRuta;
    }

    public static void setIdRuta(String idRuta) {
        Ruta.idRuta = idRuta;
    }

    public int getId_ruta() {
        return id_ruta;
    }

    public void setId_ruta(int id_ruta) {
        this.id_ruta = id_ruta;
    }

    public int getId_estado() {
        return id_estado;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public String getNameruta() {
        return nameruta;
    }

    public void setNameruta(String nameruta) {
        this.nameruta = nameruta;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getLatitudInicial() {
        return latitudInicial;
    }

    public void setLatitudInicial(String latitudInicial) {
        this.latitudInicial = latitudInicial;
    }

    public String getLongitudInicial() {
        return longitudInicial;
    }

    public void setLongitudInicial(String longitudInicial) {
        this.longitudInicial = longitudInicial;
    }

    public String getLatitudFinal() {
        return latitudFinal;
    }

    public void setLatitudFinal(String latitudFinal) {
        this.latitudFinal = latitudFinal;
    }

    public String getLongitudFinal() {
        return longitudFinal;
    }

    public void setLongitudFinal(String longitudFinal) {
        this.longitudFinal = longitudFinal;
    }
}
