package com.g2l.speedg2l.cliente;

import com.g2l.speedg2l.entidades.Jugador;
import com.g2l.speedg2l.entidades.Jugadores;
import com.g2l.speedg2l.pantallas.PantallaJuego;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;


public class HiloCliente extends Thread{

    private InetAddress direccionServer;
    private int puertoServer = 6412;
    private DatagramSocket puertoCliente;
    private boolean fin = false;

    private HashMap<Jugadores, int[]> posicionJugadores = new HashMap<>();

    public HiloCliente(){
        try {
            direccionServer = InetAddress.getByName("255.255.255.255");
            puertoCliente = new DatagramSocket();
            inicializarHashMap();
            enviarMensaje("Conexion");
        }catch (SocketException | UnknownHostException evento){
            evento.printStackTrace();
        }
    }

    private void inicializarHashMap() {
        int[] arrayDeCeros = {0, 33};
        for(int i=0; i<Jugadores.values().length; i++){
            posicionJugadores.put(Jugadores.values()[i], arrayDeCeros);
        }
    }

    public void enviarMensaje(String mensaje){
        byte[] data = mensaje.getBytes();
        DatagramPacket dp = new DatagramPacket(data, data.length, direccionServer, puertoServer);
        try {
            puertoCliente.send(dp);
        }catch (IOException event){
            event.printStackTrace();
        }
    }

    @Override
    public void run() {
        do{
            byte [] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            try{
                System.out.println("Mensaje: " + dp.toString());
                puertoCliente.receive(dp);
            }catch (IOException event){
                event.printStackTrace();
            }
            procesarMensaje(dp);
        }while(!fin);
    }

    private void procesarMensaje(DatagramPacket dp){
        String mensaje = (new String (dp.getData())).trim();
        if (mensaje.equals("OK")){
            direccionServer = dp.getAddress();
        }
        if(mensaje.equals("Empezar")){

        }
        String[] mensajePorPartes = mensaje.split("-");
        if (mensajePorPartes[0].equals("Movimiento")) {
            final int X=0, Y=1;
            int[] posiciones = new int[2];
            if (mensajePorPartes[1].equals("PosicionX")) {
                posiciones[X] = Integer.parseInt(mensajePorPartes[2]);
            }
            else if (mensajePorPartes[1].equals("PosicionY")){
                posiciones[Y] = Integer.parseInt(mensajePorPartes[2]);
            }
            if (mensajePorPartes[3].equals("JUGADOR_1")){
                posicionJugadores.put(Jugadores.JUGADOR_1, posiciones);
            }
            else if (mensajePorPartes[3].equals("JUGADOR_2")) {
                posicionJugadores.put(Jugadores.JUGADOR_2, posiciones);
            }
        }
    }

    public int[] getPosicionJugadores(Jugadores jugadorElegido) {
        return posicionJugadores.get(jugadorElegido);
    }
}
