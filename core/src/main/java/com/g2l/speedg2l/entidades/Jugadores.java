package com.g2l.speedg2l.entidades;

import com.g2l.speedg2l.utilidades.Recursos;

public enum Jugadores {
    JUGADOR_1(Recursos.JUGADOR_1),
    JUGADOR_2(Recursos.JUGADOR_2);

    private String ruta;

    Jugadores(String ruta){
        this.ruta = ruta;
    }

    public Jugador crearJugador(){
        return new Jugador(this, 70f, 70f, 0,0, ruta);
    }
}




