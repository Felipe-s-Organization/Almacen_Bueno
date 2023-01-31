package com.example.almacen_2;

import androidx.annotation.NonNull;

public class Categoria {
    private String nom;

    public Categoria() {
    }

    public Categoria(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @NonNull
    @Override
    public String toString(){
        return nom;
    }
}
