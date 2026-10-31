package com.g2l.speedg2l.entidades;

import com.badlogic.gdx.math.Rectangle;
import com.g2l.speedg2l.animaciones.AnimacionEntidad;
import com.g2l.speedg2l.cliente.HiloCliente;
import com.g2l.speedg2l.cliente.ProtocolosRed;
import com.g2l.speedg2l.utilidades.Direccion;
import com.g2l.speedg2l.utilidades.Entradas;

import java.util.ArrayList;

public class Jugador extends Entidad{

    private Jugadores numeroJugador;
    private boolean local;
//    private boolean acelerandoDerecha = false;
//    private boolean acelerandoIzquierda = false;
    private boolean moviendose=false;
    private boolean mirandoDerecha= true;
    private AnimacionEntidad animacion;

    private boolean izquierdaAnterior;
    private boolean derechaAnterior;
    private boolean arribaAnterior;


    public Jugador(Jugadores numeroJugador, float ancho, float alto, float posicionX, float posicionY, String ruta){
        super(ancho, alto, posicionX, posicionY);
        animacion = new AnimacionEntidad(ruta, 0.09f, 4, 3);
        animacion.iniciar();
        this.numeroJugador = numeroJugador;
        this.local=false;
    }

    public void asignarPosicion (float posicionX, float posicionY) {
        this.posicionX = posicionX;
        this.posicionY = posicionY;
    }

    public void animar(float delta) {
//        if (acelerandoDerecha || acelerandoIzquierda) {
//            animacion.animar(delta);
//        }
        if (moviendose) {
            animacion.animar(delta);
        }
    }

    public void dibujar() {
        animacion.dibujar(
            posicionX,
            posicionY,
            getAncho(),
            getAlto(),
            mirandoDerecha
        );
    }

    public void cerrar(){
        animacion.cerrar();
    }

    public void moverJugador(Entradas entradas, HiloCliente hiloCliente){
        boolean izquierdaActual = entradas.izquierda();
        boolean derechaActual = entradas.derecha();
        boolean arribaActual = entradas.arriba();

        if(izquierdaActual != izquierdaAnterior) {
            if (izquierdaActual) {
//                acelerandoIzquierda = true;
                moviendose = true;
                mirandoDerecha = false;
                hiloCliente.enviarMensaje(
                    ProtocolosRed.APRETE +
                    ProtocolosRed.SEPARADOR +
                    ProtocolosRed.IZQUIERDA
                );
            } else {
//                acelerandoIzquierda = false;
                moviendose = false;
                hiloCliente.enviarMensaje(
                    ProtocolosRed.NO_APRETE +
                    ProtocolosRed.SEPARADOR +
                    ProtocolosRed.IZQUIERDA
                );
            }
            izquierdaAnterior = izquierdaActual;
        }

        if(derechaActual != derechaAnterior) {
            if(derechaActual) {
//                acelerandoDerecha = true;
                moviendose = true;
                mirandoDerecha= true;
                hiloCliente.enviarMensaje(
                    ProtocolosRed.APRETE +
                    ProtocolosRed.SEPARADOR +
                    ProtocolosRed.DERECHA
                );
            }else{
//                acelerandoDerecha = false;
                moviendose = false;
                hiloCliente.enviarMensaje(
                    ProtocolosRed.NO_APRETE +
                    ProtocolosRed.SEPARADOR +
                    ProtocolosRed.DERECHA
                );
            }
            derechaAnterior = derechaActual;
        }

        if(arribaActual && !arribaAnterior){
            hiloCliente.enviarMensaje(
                ProtocolosRed.APRETE +
                ProtocolosRed.SEPARADOR +
                ProtocolosRed.ARRIBA
            );
        }
        arribaAnterior = arribaActual;
    }

    public void asignarLocal() {
        this.local = true;
    }

    public void setMoviendose(boolean moviendose) {
        if(!local){
            this.moviendose = moviendose;
        }
    }

    public void setMirandoDerecha(boolean mirandoDerecha){
        this.mirandoDerecha = mirandoDerecha;
    }

    public Jugadores getNumeroJugador() {
        return numeroJugador;
    }

    public boolean isLocal() {
        return local;
    }
}
