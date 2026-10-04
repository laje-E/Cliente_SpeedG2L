package com.g2l.speedg2l.cliente;

import java.io.IOException;
import java.net.*;


public class HiloCliente extends Thread{

    private InetAddress direccionServer;
    private int puertoServer = 6412;
    private DatagramSocket puertoCliente;
    private boolean fin = false;

    public HiloCliente(){
        try {
            direccionServer = InetAddress.getByName("255.255.255.255");
            puertoCliente = new DatagramSocket();
            enviarMensaje("Conexion", direccionServer, puertoServer);
        }catch (SocketException | UnknownHostException evento){
            evento.printStackTrace();
        }
    }

    public void enviarMensaje(String mensaje, InetAddress ipDestino, int puerto){
        byte[] data = mensaje.getBytes();
        DatagramPacket dp = new DatagramPacket(data, data.length, ipDestino, puerto);
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
    }

}
