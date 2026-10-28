package com.g2l.speedg2l.entidades;

import com.badlogic.gdx.math.Rectangle;
import com.g2l.speedg2l.animaciones.AnimacionEntidad;
import com.g2l.speedg2l.cliente.HiloCliente;
import com.g2l.speedg2l.utilidades.Direccion;
import com.g2l.speedg2l.utilidades.Entradas;

import java.util.ArrayList;

public class Jugador extends Entidad{

    private boolean acelerandoDerecha = false;
    private boolean acelerandoIzquierda = false;
    private AnimacionEntidad animacion;

    private boolean izquierdaAnterior;
    private boolean derechaAnterior;
    private boolean arribaAnterior;

    public void asignarPosicionJugador (float posicionX, float posicionY) {
        this.posicionX = posicionX;
        this.posicionY = posicionY;
    }

    public Jugador(float ancho , float alto, float posicionX, float posicionY, String ruta){
        super(ancho, alto, posicionX, posicionY);
        animacion = new AnimacionEntidad(ruta, 0.09f, 4, 3);
        animacion.iniciar();
    }

    public void animar(float delta) {
        if (acelerandoDerecha || acelerandoIzquierda) {
            animacion.animar(delta);
        }
    }

    public void dibujar() {
        animacion.dibujar(
            posicionX,
            posicionY,
            getAncho(),
            getAlto()
        );
    }

    public void cerrar(){
        animacion.cerrar();
    }

    public void moverJugador(Entradas entradas, Jugadores numeroJugador, HiloCliente hiloCliente){
        boolean izquierdaActual = entradas.izquierda();
        boolean derechaActual = entradas.derecha();
        boolean arribaActual = entradas.arriba();

        if(izquierdaActual != izquierdaAnterior) {
            if (izquierdaActual) {
                acelerandoIzquierda = true;
                hiloCliente.enviarMensaje("Aprete-Izquierda");
            } else {
                acelerandoIzquierda = false;
                hiloCliente.enviarMensaje("NoAprete-Izquierda");
            }
            izquierdaAnterior = izquierdaActual;
        }

        if(derechaActual != derechaAnterior) {
            if(derechaActual) {
                acelerandoDerecha = true;
                hiloCliente.enviarMensaje("Aprete-Derecha");
            }else{
                acelerandoDerecha = false;
                hiloCliente.enviarMensaje("NoAprete-Derecha");
            }
            derechaAnterior = derechaActual;
        }

        if(arribaActual && !arribaAnterior){
            hiloCliente.enviarMensaje("Aprete-Arriba");
        }
        arribaAnterior = arribaActual;
    }
}
