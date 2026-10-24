package com.g2l.speedg2l.cliente;

import com.g2l.speedg2l.entidades.Jugadores;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;


public class HiloCliente extends Thread{

    private InetAddress direccionServer;
    private int puertoServer = 6412;
    private DatagramSocket puertoCliente;
    private boolean fin = false;

    private HashMap<Jugadores, float[]> posicionJugadores = new HashMap<>();

    private int numeroJugadorCliente;

    private PosicionFinal[] posicionesFinales = new PosicionFinal[2];
    private boolean esteClienteTermino = false;
    private boolean todosTerminaron = false;

    private boolean empieza = false;

    private boolean servidorConfirmado = false;
    private boolean servidorDesconectado = false;

    private long ultimoMensaje = 0;

    private final long TIEMPO_MAXIMO_SIN_RESPUESTA = 5000;

    public HiloCliente(){
        try {
            direccionServer = InetAddress.getByName("255.255.255.255");
            puertoCliente = new DatagramSocket();
            puertoCliente.setSoTimeout(1000);
            inicializarHashMap();
        }catch (SocketException | UnknownHostException evento){
            evento.printStackTrace();
        }
    }

    private void inicializarHashMap() {
        float[] arrayDeCeros = {0, 33};
        for(int i=0; i<Jugadores.values().length; i++){
            posicionJugadores.put(Jugadores.values()[i], arrayDeCeros);
        }
    }

    public void enviarMensaje(String mensaje){
        if(puertoCliente == null || puertoCliente.isClosed()){
            System.err.println("Error, cliente no disponible para enviar mensajes.");
        }else{
            byte[] data = mensaje.getBytes();
            DatagramPacket dp = new DatagramPacket(data, data.length, direccionServer, puertoServer);
            try {
                puertoCliente.send(dp);
            }catch (IOException event){
                event.printStackTrace();
            }
        }
    }

    @Override
    public void run(){
        if(puertoCliente == null){
            System.err.println("El socket del cliente no está disponible.");
            return;
        }

        while(!puertoCliente.isClosed()) {

            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);

            try{
                puertoCliente.receive(dp);

                // Llegó algo del servidor: actualizamos el último contacto
                ultimoMensaje = System.currentTimeMillis();
                procesarMensaje(dp);

            }catch (SocketTimeoutException timeout) {

                // El tiempo de espera no debe finalizar el hilo.
                if (servidorConfirmado && !servidorDesconectado) {
                    long ahora = System.currentTimeMillis();

                    if (ahora - ultimoMensaje >= TIEMPO_MAXIMO_SIN_RESPUESTA) {
                        servidorDesconectado = true;

                        System.out.println(
                            "El servidor se desconectó."
                        );
                    }
                }

            }catch (SocketException exception) {
                // Cerrar el socket del cliente es una finalización normal.
                if (!puertoCliente.isClosed()){
                    exception.printStackTrace();
                }

            }catch (IOException event) {
                if(puertoCliente.isClosed()) {
                    break;
                }
                event.printStackTrace();
                break;
            }

            if(puertoCliente.isClosed()) {
                break;
            }

            if (servidorConfirmado && !servidorDesconectado) {
                long tiempoSinRespuesta =
                    System.currentTimeMillis() - ultimoMensaje;

                if (tiempoSinRespuesta >
                    TIEMPO_MAXIMO_SIN_RESPUESTA) {

                    servidorDesconectado = true;

                    System.out.println(
                        "El servidor dejó de responder."
                    );
                }
            }

        }

        System.out.println("HiloCliente terminado.");
    }

    private void procesarMensaje(DatagramPacket dp){
        String mensaje = (new String (dp.getData())).trim();
        if (mensaje.equals("Hola")) {
            servidorConfirmado = true;
            enviarMensaje("Hola");
        }
        if (mensaje.equals("OK")){
            direccionServer = dp.getAddress();
            servidorConfirmado = true;
            servidorDesconectado = false;
        }
        if(mensaje.equals("Empezar")){
            empieza = true;
        }
        String[] mensajePorPartes = mensaje.split("-");
        if(mensajePorPartes[0].equals("NumeroJugador")){
            if(mensajePorPartes[1].equals("JUGADOR_1")){
                numeroJugadorCliente = 1;
            }
            else if(mensajePorPartes[1].equals("JUGADOR_2")){
                numeroJugadorCliente = 2;
            }
        }
        if (mensajePorPartes[0].equals("Movimiento")) {
            final int X=0, Y=1;
            float[] posiciones = new float[2];
            posiciones[X] = Float.parseFloat(mensajePorPartes[1]);
            posiciones[Y] = Float.parseFloat(mensajePorPartes[2]);
            if (mensajePorPartes[3].equals("JUGADOR_1")){
                posicionJugadores.put(Jugadores.JUGADOR_1, posiciones);
            }
            else if (mensajePorPartes[3].equals("JUGADOR_2")) {
                posicionJugadores.put(Jugadores.JUGADOR_2, posiciones);
            }
        }

        if(mensajePorPartes[0].equals("LLEGO")){
            if(numeroJugadorCliente == Integer.parseInt(mensajePorPartes[1])){
                esteClienteTermino = true;
            }

            int idJugador = Integer.parseInt(mensajePorPartes[1]);
            int posicion = Integer.parseInt(mensajePorPartes[2]);
            float tiempo = Float.parseFloat(mensajePorPartes[3]);
            posicionesFinales[posicion - 1] =
                new PosicionFinal(tiempo, idJugador, posicion);

            if(posicion == 2){
                todosTerminaron = true;
            }
        }

    }

    public PosicionFinal[] getPosicionesFinales() {
        return posicionesFinales;
    }

    public boolean isEsteClienteTermino() {
        return esteClienteTermino;
    }

    public boolean isTodosTerminaron() {
        return todosTerminaron;
    }

    //private void procesarMensaje(DatagramPacket dp) {
//
//    String mensaje = new String(dp.getData(), 0, dp.getLength()).trim();
//
//    System.out.println("[CLIENTE] Recibido: '" + mensaje + "'");
//
//    if (mensaje.equals("OK")) {
//        direccionServer = dp.getAddress();
//        return;
//    }
//
//    if (mensaje.equals("Empezar")) {
//        return;
//    }
//
//    String[] mensajePorPartes = mensaje.split("-");
//
//    if (!mensajePorPartes[0].equals("Movimiento")) {
//        return;
//    }
//
//    if (mensajePorPartes.length != 4) {
//        System.out.println("[CLIENTE] Paquete Movimiento inválido.");
//        return;
//    }
//
//    if (mensajePorPartes[1].isEmpty() || mensajePorPartes[2].isEmpty()) {
//        System.out.println("[CLIENTE] Movimiento con posición vacía.");
//        return;
//    }
//
//    try {
//        float[] posiciones = new float[2];
//
//        posiciones[0] = Float.parseFloat(mensajePorPartes[1]);
//        posiciones[1] = Float.parseFloat(mensajePorPartes[2]);
//
//        if (mensajePorPartes[3].equals("JUGADOR_1")) {
//            posicionJugadores.put(
//                Jugadores.JUGADOR_1,
//                posiciones
//            );
//        }
//        else if (mensajePorPartes[3].equals("JUGADOR_2")) {
//            posicionJugadores.put(
//                Jugadores.JUGADOR_2,
//                posiciones
//            );
//        }
//
//    } catch (NumberFormatException e) {
//        System.out.println(
//            "[CLIENTE] Posición inválida: '" +
//                mensaje +
//                "'"
//        );
//    }
//}

    public float[] getPosicionJugadores(Jugadores jugadorElegido) {
        return posicionJugadores.get(jugadorElegido);
    }


    public void finalizarHilo() {
        if (puertoCliente != null) {
            puertoCliente.close();
            fin=true;
        }else{
            System.out.println("Flaco, no se cierra.");
        }
    }
    public boolean isEmpieza() {
        return empieza;
    }

    public int getNumeroJugador() {
        return numeroJugadorCliente;
    }

    public boolean isServidorDesconectado() {
        return servidorDesconectado;
    }

    public boolean isServidorConfirmado() {
        return servidorConfirmado;
    }
}
