package com.example.logistica;

public class Rutas {
    private int id_ruta, id_estado;
    private String nameruta, origen, destino;
    private String latitudInicial;
    private String longitudInicial;
    private String latitudFinal;
    private String longitudFinal;
    private String datos;
    protected static String idRuta;
    protected static Integer fragmento;


    public Rutas() {
    }

    public Rutas(int id_ruta,
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
        this.datos = this.nameruta+" | "+this.origen+" | "+this.destino;

    }

    public String getDatos() {
        return datos;
    }

    public static String getIdRuta() {
        return idRuta;
    }

    public static void setIdRuta(String idRuta) {
        Rutas.idRuta = idRuta;
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

    public static void setFragmento(int frag){ fragmento = frag;}
    public static int getFragmento(){
        return fragmento;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public static void setFragmento(Integer fragmento) {
        Rutas.fragmento = fragmento;
    }
}
