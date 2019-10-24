package com.example.arisoft.controlsr.Modelo;

public class Recibido {
    String folio,codigo,codigo2, descripcion,posicion;
    Float cantidad,surtidas,por_surtir;

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Float getSurtidas() {
        return surtidas;
    }

    public void setSurtidas(Float surtidas) {
        this.surtidas = surtidas;
    }

    public Float getPor_surtir() {
        return por_surtir;
    }

    public void setPor_surtir(Float por_surtir) {
        this.por_surtir = por_surtir;
    }

    public Recibido(String folio, String codigo, String codigo2, String descripcion, Float cantidad, Float surtidas, Float por_surtir, String posicion ){
        this.folio = folio;
        this.codigo = codigo;
        this.codigo2 = codigo2;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.surtidas = surtidas;
        this.por_surtir = por_surtir;
        this.posicion = posicion;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo2() {
        return codigo2;
    }

    public void setCodigo2(String codigo2) {
        this.codigo2 = codigo2;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
