package com.example.almacen_2;

public class Producto {
    private String codi;
    private String nom;
    private String categoria;
    private int cajas;
    private int unidades;
    private int cantidad;
    private double precio;


    public Producto() {
    }

    public Producto(String codi, String nom,String categoria, int cajas, int unidades, int cantidad, double precio) {
        this.codi = codi;
        this.nom = nom;
        this.categoria = categoria;
        this.cajas = cajas;
        this.unidades = unidades;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public String getCodi() {
        return codi;
    }

    public void setCodi(String codi) {
        this.codi = codi;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCajas() {
        return cajas;
    }

    public void setCajas(int cajas) {
        this.cajas = cajas;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
