package com.g2l.speedg2l.cliente;

import com.g2l.speedg2l.componentes.Texto;

public class PosicionFinal {

    private float tiempo;
    private int numeroPosicion;
    private int idJugador;

    public PosicionFinal(float tiempo, int idJugador, int numeroPosicion){
        this.tiempo = tiempo;
        this.idJugador = idJugador;
        this.numeroPosicion = numeroPosicion;
    }

    public float getTiempo() {
        return tiempo;
    }

    public int getNumeroPosicion() {
        return numeroPosicion;
    }

    public int getIdJugador() {
        return idJugador;
    }

}
